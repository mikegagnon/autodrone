package me.michaelgagnon.chopper

// TODO: cleanup

import scala.util.parsing.combinator._
import scala.util.parsing.input._
import scala.scalajs.js.Dynamic.global

/* Tokens *****************************************************************************************/

sealed trait MeasurementUnitType
case object METERS extends MeasurementUnitType
case object METERS_SEC extends MeasurementUnitType
case object METERES_SEC_2 extends MeasurementUnitType

sealed trait ComparisonOperator
sealed trait Value

sealed trait Token
object IF extends Token
object THEN extends Token
object ELSE extends Token
object AND extends Token
object OR extends Token
object NOT extends Token
object TRUE extends Token
object FALSE extends Token
object OPENPAREN extends Token
object CLOSEPAREN extends Token
object OPENCURLY extends Token
object CLOSECURLY extends Token
object LESSTHAN extends Token with ComparisonOperator
object LESSTHANEQUALS extends Token with ComparisonOperator
object GREATERTHAN extends Token with ComparisonOperator
object GREATERTHANEQUALS extends Token with ComparisonOperator
object EQUALS extends Token with ComparisonOperator
object ASSIGN extends Token
case class MEASUREMENTUNIT(unit: MeasurementUnitType) extends Token
case class IDENTIFIER(id: String) extends Token with Value
case class DOUBLELITERAL(value: Double) extends Token //with Value

/* Lexer ******************************************************************************************/

sealed trait CompilationError
case class LexerError(msg: String) extends CompilationError

object Lexer extends RegexParsers {
  override def skipWhitespace = true
  override val whiteSpace = "[ \t\r\f\n]+".r

  def measurementUnit: Parser[MEASUREMENTUNIT] = {
    """meters/second\^2\b|meters/second\b|meters\b|meter\\b""".r ^^ {
      case "meters" => MEASUREMENTUNIT(METERS)
      case "meter" => MEASUREMENTUNIT(METERS)
      case "meters/second" => MEASUREMENTUNIT(METERS_SEC)
      case "meters/second^2" => MEASUREMENTUNIT(METERES_SEC_2)
    }
  }

  def identifier: Parser[IDENTIFIER] = {
    "[a-zA-Z_][a-zA-Z0-9_]*\\b".r ^^ { str => IDENTIFIER(str) }
  }

  def double: Parser[DOUBLELITERAL] = {
    """[-+]?[0-9]*\.?[0-9]?[0-9]?[0-9]?[0-9]\b""".r ^^ { str =>
      DOUBLELITERAL(str.toDouble)
    }
  }

  def _true: Parser[DOUBLELITERAL] = {
    """true\b""".r ^^ { str => DOUBLELITERAL(1.0)}
  }

  def _false: Parser[DOUBLELITERAL] = {
    """false\b""".r ^^ { str => DOUBLELITERAL(0.0)}
  }

  def _if               = "if"    ^^ (_ => IF)
  def _then             = "then"  ^^ (_ => THEN)
  def _else             = "else"  ^^ (_ => ELSE)
  def _and              = "and"   ^^ (_ => AND)
  def _or               = "or"    ^^ (_ => OR)
  def _not              = "not"   ^^ (_ => NOT)
  def openParen         = "("     ^^ (_ => OPENPAREN)
  def closeParen        = ")"     ^^ (_ => CLOSEPAREN)
  def openCurly         = "{"     ^^ (_ => OPENCURLY)
  def closeCurly        = "}"     ^^ (_ => CLOSECURLY)
  def lessThan          = "<"     ^^ (_ => LESSTHAN)
  def lessThanEquals    = "<="    ^^ (_ => LESSTHANEQUALS)
  def greaterThan       = ">"     ^^ (_ => GREATERTHAN)
  def greaterThanEquals = ">="    ^^ (_ => GREATERTHANEQUALS)
  def equals            = "=="    ^^ (_ => EQUALS)
  def assign            = "="     ^^ (_ => ASSIGN)

  def reserved = (
      _if |
      _then |
      _else |
      _and |
      _or |
      _not |
      _true |
      _false |
      openParen |
      closeParen |
      openCurly |
      closeCurly |
      lessThan |
      lessThanEquals |
      greaterThan |
      greaterThanEquals |
      measurementUnit |
      equals |
      assign |
      identifier |
      double)

  def ident = not(reserved) ~> identifier

  // TODO: what about rawTokens => rawTokens
  def tokens: Parser[List[Token]] = {
    phrase(rep(reserved|ident)) ^^ { rawTokens => rawTokens }
  }


  def apply(text: String): Either[LexerError, List[Token]] = {
    parse(tokens, text.trim) match {
      case NoSuccess(msg, next) => Left(LexerError(msg))
      case Success(result, next) => Right(result)
    }
  }
}

/* Parser *****************************************************************************************/

case class ChopperParserError(msg: String) extends CompilationError


object ChopperParser extends Parsers {

  sealed trait ChopperAst

  override type Elem = Token

  class TokenReader(tokens: Seq[Token]) extends Reader[Token] {
    override def first: Token = tokens.head
    override def atEnd: Boolean = tokens.isEmpty
    override def pos: Position = NoPosition
    override def rest: Reader[Token] = new TokenReader(tokens.tail)
  }

  lazy val  program: Parser[Statements] = phrase(block)
  
  lazy val  block: Parser[Statements] = rep(statement) ^^ { case itList => Statements(itList) }

  // DOES this screw up short circuit evaluation?
  lazy val expr : Parser[Expression] =
    opt(term ~ OR) ~ term  ^^ {
      case a ~ b => Expression(b, a.map(_._1))
    }
  
  lazy val  term: Parser[Term] = 
    opt(factor ~ AND) ~ factor ^^ {
      case a ~ b => Term(b, a.map(_._1))
    }

  lazy val  factor: Parser[Factor] = booleanConst | notFactor | condition

  /*lazy val  factorIdentifier: Parser[FactorIdentifier] = 
    identifier ^^ { case id => FactorIdentifier(id) }*/

  lazy val  notFactor: Parser[FactorNot] = {
    NOT ~ factor ^^ {
      case not ~ f => FactorNot(f)
    }
  }

  lazy val  booleanConst: Parser[BooleanConst] = {
    (TRUE | FALSE) ^^ {
      case TRUE => BooleanConst(true)
      case FALSE => BooleanConst(false)
      case _ => throw new IllegalArgumentException()
    }
  }



  lazy val  condition: Parser[Condition] = {
    value ~ comparisonOperator ~ value ^^ {
      case v1 ~ op ~ v2 => Condition(v1, op, v2)
    }
  }

  lazy val value: Parser[Value] = identifier | doubleWithType

  lazy val doubleWithType: Parser[DoubleWithType] = double ~ opt(measurementUnit) ^^ {
    case d ~ m => DoubleWithType(d.value, m)
  }



  lazy val  comparisonOperator: Parser[ComparisonOperator] = {
    (LESSTHAN | LESSTHANEQUALS | GREATERTHAN | GREATERTHANEQUALS | EQUALS) ^^ {
      case o: ComparisonOperator => o
      case _ => throw new IllegalArgumentException("This shouldn't happen")
    }
  }

  /*
if (true) {
    x = 1 meter
    y = 2 meters
} else if (false and true) {
  z = 3 meters
} else if (true or true) {
  x = 1 meter
} else {
  z = 2 meters
}
  */

  case class DoubleWithType(value: Double, measurementUnit: Option[MEASUREMENTUNIT]) extends Value
  case class Expression(term1: Term, term2: Option[Term]) extends ChopperAst
  case class Term(factor1: Factor, factor2: Option[Factor]) extends ChopperAst
  sealed trait Factor extends ChopperAst
  //case class FactorIdentifier(id: IDENTIFIER) extends Factor
  case class FactorNot(f: Factor) extends Factor
  case class BooleanConst(value: Boolean) extends Factor
  case class Condition(lv: Value, op: ComparisonOperator, rv: Value) extends Factor


  lazy val  ifClause : Parser[IfClause] =
    IF ~ OPENPAREN ~ expr ~ CLOSEPAREN ~ opt(THEN) ~ OPENCURLY ~ block ~ CLOSECURLY ^^ {
      case if_ ~ op ~ e ~ cp ~ then_ ~ oc ~ b ~ cc => IfClause(e, b)
    }
  
  lazy val  elseIfClause : Parser[ElseIfClause] =
    ELSE ~ ifClause ^^ {
      case else_ ~ ic => ElseIfClause(ic)
    }
  
  lazy val  elseClause : Parser[ElseClause] = ELSE ~ opt(THEN) ~ OPENCURLY ~ block ~ CLOSECURLY ^^ {
      case else_ ~ then_ ~ oc ~ b ~ cc => ElseClause(b)
    }
  
  lazy val  ifElse : Parser[IfElseIfElse] = ifClause ~ opt(rep1(elseIfClause)) ~ opt(elseClause) ^^ {
      case ic ~ eic ~ ec => IfElseIfElse(ic, eic, ec)
    }
  
  case class IfClause(expression: Expression, thenBlock: Statements) extends ChopperAst
  case class ElseIfClause(ifClause: IfClause) extends ChopperAst
  case class ElseClause(thenBlock: Statements) extends ChopperAst
  case class IfElseIfElse(ifClause: IfClause, elseIfClauses: Option[List[ElseIfClause]],
    elseClause: Option[ElseClause]) extends ChopperAst

  lazy val  statement: Parser[ChopperAst] = {
    assignment | ifElse
  }

  case class Assignment(variable: IDENTIFIER, value: Value) extends ChopperAst

  lazy val  assignment: Parser[Assignment] = {
    identifier ~ ASSIGN ~ value ^^ {
      case id ~ assign ~ v => Assignment(id, v)
    }
  }

  lazy val  identifier: Parser[IDENTIFIER] = {
    accept("identifier", { case id @ IDENTIFIER(name) => id })
  }

  lazy val  double: Parser[DOUBLELITERAL] = {
    accept("double literal", { case lit @ DOUBLELITERAL(name) => lit })
  }

  lazy val  measurementUnit: Parser[MEASUREMENTUNIT] = {
    accept("measurement unit", { case mu @ MEASUREMENTUNIT(name) => mu })
  }

  case class Statements(statements: List[ChopperAst]) extends ChopperAst

  //def statements
  def apply(tokens: Seq[Token]): Either[ChopperParserError, ChopperAst] = {
    val reader = new TokenReader(tokens)
    program(reader) match {
      case NoSuccess(msg, next) => Left(ChopperParserError(msg))
      case Success(result, next) => Right(result)
    }
  }

}


object Compiler {

  def apply(text: String): Either[CompilationError, ChopperParser.ChopperAst] = {
    for {
      tokens <- Lexer(text).right
      ast <- ChopperParser(tokens).right
    } yield ast
  }
}


