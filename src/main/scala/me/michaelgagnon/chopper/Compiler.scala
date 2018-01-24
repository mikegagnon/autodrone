package me.michaelgagnon.chopper

import scala.util.parsing.combinator._
import scala.util.parsing.input._
import scala.scalajs.js.Dynamic.global

/* Tokens *****************************************************************************************/

sealed trait MeasurementUnitType
case object METERS extends MeasurementUnitType
case object THRUST extends MeasurementUnitType

sealed trait ComparisonOperator

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
case class IDENTIFIER(id: String) extends Token
case class DOUBLELITERAL(value: Double) extends Token

/* Lexer ******************************************************************************************/

sealed trait CompilationError
case class LexerError(msg: String) extends CompilationError

object Lexer extends RegexParsers {
  override def skipWhitespace = true
  override val whiteSpace = "[ \t\r\f\n]+".r

  def measurementUnit: Parser[MEASUREMENTUNIT] = {
    "meters|meter|thrust".r ^^ {
      case "meters" => MEASUREMENTUNIT(METERS)
      case "meter" => MEASUREMENTUNIT(METERS)
      case "thrust" => MEASUREMENTUNIT(THRUST)
    }
  }

  def identifier: Parser[IDENTIFIER] = {
    "[a-zA-Z_][a-zA-Z0-9_]*".r ^^ { str => IDENTIFIER(str) }
  }

  def double: Parser[DOUBLELITERAL] = {
    """[-+]?[0-9]*\.?[0-9]""".r ^^ { str =>
      DOUBLELITERAL(str.toDouble)
    }
  }

  def _if               = "if"    ^^ (_ => IF)
  def _then             = "then"  ^^ (_ => THEN)
  def _else             = "else"  ^^ (_ => ELSE)
  def _and              = "and"   ^^ (_ => AND)
  def _or               = "or"    ^^ (_ => OR)
  def _not              = "not"   ^^ (_ => NOT)
  def _true             = "true"   ^^ (_ => TRUE)
  def _false            = "false"   ^^ (_ => FALSE)
  def openParen         = "("     ^^ (_ => OPENPAREN)
  def closeParen        = ")"     ^^ (_ => CLOSEPAREN)
  def openCurly         = "{"     ^^ (_ => OPENCURLY)
  def closeCurly        = "}"     ^^ (_ => CLOSECURLY)
  def lessThan          = "<"     ^^ (_ => LESSTHAN)
  def lessThanEquals    = "<="    ^^ (_ => LESSTHANEQUALS)
  def greaterThan       = ">"     ^^ (_ => GREATERTHAN)
  def greaterThanEquals = ">="    ^^ (_ => GREATERTHANEQUALS)
  def assign            = "="     ^^ (_ => ASSIGN)

  // TODO: what about rawTokens => rawTokens
  def tokens: Parser[List[Token]] = {
    phrase(rep1(
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
      assign |
      measurementUnit |
      double |
      identifier)) ^^ { rawTokens => rawTokens }
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


/*
  sealed trait Condition {
    val factName: IDENTIFIER
    val value: DOUBLELITERAL
  }
  // TODO ?
  case class LessThan(factName: IDENTIFIER, value: DOUBLELITERAL) extends Condition
  case class LessThanEquals(factName: IDENTIFIER, value: DOUBLELITERAL) extends Condition
  case class GreaterThan(factName: IDENTIFIER, value: DOUBLELITERAL) extends Condition

  case class Statements(statements: List[ChopperAst]) extends ChopperAst

  // TODO ?
  // toodo: thenBlock : Assignment
  case class IfThen(predicate: Condition, thenBlock: Assignment) extends ChopperAst
  // value?
  case class Assignment(variable: IDENTIFIER, value: DOUBLELITERAL)

  // TODO: name
  private def identifier: Parser[IDENTIFIER] = {
    accept("identifier", { case id @ IDENTIFIER(name) => id })
  }

  private def double: Parser[DOUBLELITERAL] = {
    accept("double literal", { case lit @ DOUBLELITERAL(name) => lit })
  }

  def measurementUnit: Parser[MEASUREMENTUNIT] = {
    accept("measurement unit", { case mu @ MEASUREMENTUNIT(name) => mu })
  }

  def condition: Parser[Condition] = {
    val lt = (identifier ~ LESSTHAN ~ double ~ measurementUnit)    ^^ { case id ~ lt ~ d ~ mu => LessThan(id, d) }
    val gt = (identifier ~ GREATERTHAN ~ double ~ measurementUnit) ^^ { case id ~ lt ~ d ~ mu => GreaterThan(id, d) }
    lt | gt
  }

  def assignment: Parser[Assignment] = {
    identifier ~ ASSIGN ~ double ~ measurementUnit ^^ {
      case id ~ assign ~ d ~mu => Assignment(id, d)
    }
  }

  def program: Parser[ChopperAst] = {
    phrase(block)
  }

  def block: Parser[ChopperAst] = {
    rep1(ifThen) ^^ { case itList => Statements(itList) }
  }

  def ifThen: Parser[ChopperAst] = {
    IF ~ OPENPAREN ~ condition ~ CLOSEPAREN ~
    THEN ~ OPENPAREN ~ assignment ~ CLOSEPAREN ^^ {
      case _if ~ op1 ~ c ~ cp1 ~ _then ~ op2 ~ a ~ cp2 => IfThen(c, a)
    }
  }

*/


  def program: Parser[Statements] = phrase(block)
  
  def block: Parser[Statements] = rep(statement) ^^ { case itList => Statements(itList) }

  def expr : Parser[Expression] =
    term ~ opt(OR ~ term) ^^ {
      case a ~ b => Expression(a, b.map(_._2))
    }
  
  def term: Parser[Term] = 
    factor ~ opt(AND ~ factor) ^^ {
      case a ~ b => Term(a, b.map(_._2))
    }

  def factor: Parser[Factor] = booleanConst | notFactor | parenFactor | factorIdentifier | condition

  def factorIdentifier: Parser[FactorIdentifier] = 
    identifier ^^ { case id => FactorIdentifier(id) }


  def parenFactor: Parser[ParenFactor] = {
    OPENPAREN ~ expr ~ CLOSEPAREN ^^ {
      case op ~ e ~ cp => ParenFactor(e)
    }
  }

  def notFactor: Parser[FactorNot] = {
    NOT ~ factor ^^ {
      case not ~ f => FactorNot(f)
    }
  }

  def booleanConst: Parser[BooleanConst] = {
    (TRUE | FALSE) ^^ {
      case TRUE => BooleanConst(true)
      case FALSE => BooleanConst(false)
      case _ => throw new IllegalArgumentException()
    }
  }

  def condition: Parser[Condition] = {
    expr ~ comparisonOperator ~ expr ^^ {
      case e1 ~ op ~ e2 => Condition(e1, op, e2)
    }
  }

  def comparisonOperator: Parser[ComparisonOperator] = {
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

  case class Expression(term1: Term, term2: Option[Term]) extends ChopperAst
  case class Term(factor1: Factor, factor2: Option[Factor]) extends ChopperAst
  sealed trait Factor extends ChopperAst
  case class FactorIdentifier(id: IDENTIFIER) extends Factor
  case class ParenFactor(e: Expression) extends Factor
  case class FactorNot(f: Factor) extends Factor
  case class BooleanConst(value: Boolean) extends Factor
  case class Condition(le: Expression, op: ComparisonOperator, re: Expression) extends Factor

  def ifClause : Parser[IfClause] =
    IF ~ OPENPAREN ~ expr ~ CLOSEPAREN ~ opt(THEN) ~ OPENCURLY ~ block ~ CLOSECURLY ^^ {
      case if_ ~ op ~ e ~ cp ~ then_ ~ oc ~ b ~ cc => IfClause(e, b)
    }
  
  def elseIfClause : Parser[ElseIfClause] =
    ELSE ~ ifClause ^^ {
      case else_ ~ ic => ElseIfClause(ic)
    }
  
  def elseClause : Parser[ElseClause] = ELSE ~ opt(THEN) ~ OPENCURLY ~ block ~ CLOSECURLY ^^ {
      case else_ ~ then_ ~ oc ~ b ~ cc => ElseClause(b)
    }
  
  def ifElse : Parser[IfElseIfElse] = ifClause ~ opt(rep1(elseIfClause)) ~ opt(elseClause) ^^ {
      case ic ~ eic ~ ec => IfElseIfElse(ic, eic, ec)
    }
  

  //case class Expression()
  case class IfClause(expression: Expression, thenBlock: Statements) extends ChopperAst
  case class ElseIfClause(ifClause: IfClause) extends ChopperAst
  case class ElseClause(thenBlock: Statements) extends ChopperAst
  case class IfElseIfElse(ifClause: IfClause, elseIfClauses: Option[List[ElseIfClause]],
    elseClause: Option[ElseClause]) extends ChopperAst

  def statement: Parser[ChopperAst] = {
    assignment | ifElse
  }

  case class Assignment(variable: IDENTIFIER, value: DOUBLELITERAL) extends ChopperAst
  def assignment: Parser[Assignment] = {
    identifier ~ ASSIGN ~ double ~ measurementUnit ^^ {
      case id ~ assign ~ d ~ mu => Assignment(id, d)
    }
  }

  private def identifier: Parser[IDENTIFIER] = {
    accept("identifier", { case id @ IDENTIFIER(name) => id })
  }

  private def double: Parser[DOUBLELITERAL] = {
    accept("double literal", { case lit @ DOUBLELITERAL(name) => lit })
  }

  def measurementUnit: Parser[MEASUREMENTUNIT] = {
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

  def compile() {
    val text: String = global.cm.getValue().asInstanceOf[String]
    //println(text)
  }

}


