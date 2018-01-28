package me.michaelgagnon.chopper

import scala.collection.mutable
import ChopperParser._

// TODO: do we really need to store id here?
case class Variable(id: String, typ: Option[MeasurementUnitType], var value: Double)

case class InterpreterCrash(private val message: String) extends Exception(message) 

class State(val variables: mutable.HashMap[String, Variable])

class Interpreter() { //val state: State) {

  val state = new State(mutable.HashMap[String, Variable]())

  /** Execute Assignment **************************************************************************/

  def updateVariableFromIdentifier(destVariable: Variable, id: String) =
    state.variables.get(id) match {
      case Some(srcVariable) =>
        if (destVariable.typ == srcVariable.typ) {
          destVariable.value = srcVariable.value
        } else {
          throw new InterpreterCrash(s"Cannot assign ${srcVariable.id} to ${destVariable.id}, because their types do not match")
        }
      case None =>
        throw new InterpreterCrash(s"Cannot assign ${id} to ${destVariable.id}, ${id} is undefined")
    }

  def updateVariableFromLiteral(destVariable: Variable, v: Double, measurementUnit: Option[MeasurementUnitType]) {
    if (destVariable.typ == measurementUnit) {
      destVariable.value = v
    } else {
      throw new InterpreterCrash(s"Cannot assign ${v} to ${destVariable.id}, because their types do not match")
    }
  }

  def updateVariable(destVariable: Variable, value: Value) =
    value match {
      case IDENTIFIER(id) => updateVariableFromIdentifier(destVariable, id)
      case DoubleWithType(v, measurementUnit) => updateVariableFromLiteral(destVariable, v, measurementUnit.map(_.unit))
    }


  def newVariableFromIdentifier(destId: String, srcId: String) =
    state.variables.get(srcId) match {
      case Some(Variable(_, measurementUnit, v)) => state.variables(destId) = Variable(destId, measurementUnit, v)
      case None => throw new InterpreterCrash(s"Cannot assign ${srcId} to ${destId} because ${srcId} is not defined")
    }

  def newVariable(destId: IDENTIFIER, value: Value) =
    value match {
      case IDENTIFIER(srcId) => newVariableFromIdentifier(destId.id, srcId)
      case DoubleWithType(v, measurementUnit) => state.variables(destId.id) = Variable(destId.id, measurementUnit.map(_.unit), v)
    }

  def executeAssignment(id: IDENTIFIER, value: Value) =
    state.variables.get(id.id) match {
      case Some(variable) => updateVariable(variable, value)
      case None => newVariable(id, value)
    }

  /** Execute IfElseIfElse ************************************************************************/
  
  def evaluateFactorNot(f: Factor) = !evaluateFactor(f)

  def evaluateIdentifier(id: String): Variable =
    state.variables.get(id) match {
      case Some(variable) => variable
      case None => throw new InterpreterCrash(s"Variable $id is not defined")
    }

  def evaluateValue(value: Value): Variable = 
    value match {
      case IDENTIFIER(id) => evaluateIdentifier(id)
      case DoubleWithType(v, measurementUnit) => Variable(v.toString, measurementUnit.map(_.unit), v)
    }

  def evaluateCondition(lv: Value, op: ComparisonOperator, rv: Value) = {
    val lVar = evaluateValue(lv)
    val rVar = evaluateValue(rv)
    if (lVar.typ != rVar.typ) {
      throw new InterpreterCrash(s"You cannot compare ${lVar.id} and ${rVar.id} because their types do not match")
    }
    op match {
      case LESSTHAN => lVar.value < rVar.value
      case LESSTHANEQUALS => lVar.value <= rVar.value
      case GREATERTHAN => lVar.value > rVar.value
      case GREATERTHANEQUALS => lVar.value >= rVar.value
      case EQUALS => lVar.value == rVar.value
    }
  }

  def evaluateFactor(factor: Factor): Boolean =
    factor match {
      //case FactorIdentifier(identifier) => evaluateIdentifier(identifier.id)
      case FactorNot(f) => evaluateFactorNot(f)
      case BooleanConst(v) => v
      case Condition(lv, op, rv) => evaluateCondition(lv, op, rv)
    }

  def evaluateTerm(term: Term): Boolean = 
    term match {
      case Term(factor1, Some(factor2)) => evaluateFactor(factor1) && evaluateFactor(factor2)
      case Term(factor1, None) => evaluateFactor(factor1)
    }

  def evaluateExpression(expression: Expression): Boolean =
    expression match {
      case Expression(term1, Some(term2)) => evaluateTerm(term1) || evaluateTerm(term2)
      case Expression(term1, None) => evaluateTerm(term1)
    }

  // Returns result of if expression
  def executeIfClause(expression: Expression, thenBlock: Statements): Boolean = {
    if (evaluateExpression(expression)) {
      thenBlock.statements.foreach(executeStatement(_))
      true
    } else {
      false
    }
  }

  def executeElseIfCaluses(elseIfCaluses: List[ElseIfClause]): Boolean = {
    val trueClause: Option[ElseIfClause] = elseIfCaluses.find {
      case ElseIfClause(IfClause(expression, thenBlock)) => executeIfClause(expression, thenBlock)
    }

    return trueClause.nonEmpty
  }

  def executeIfElseIfElse(ifClause: IfClause, elseIfClauses: Option[List[ElseIfClause]],
    elseClause: Option[ElseClause]): Unit = {
     if (!executeIfClause(ifClause.expression, ifClause.thenBlock)) {
        elseIfClauses match {
          case Some(elseIfClauses) => if (!executeElseIfCaluses(elseIfClauses)) {
            elseClause match {
              case Some(ec) => ec.thenBlock.statements.foreach(executeStatement(_))
              case None => ()
            }
          }
          case None => elseClause match {
            case Some(ec) => ec.thenBlock.statements.foreach(executeStatement(_))
            case None => ()
          }
        }
     }
  }

  /** executeStatement ****************************************************************************/

  def executeStatement(statement: ChopperAst) =
    statement match {
      case Assignment(id, value) => executeAssignment(id, value)
      case IfElseIfElse(ifClause, elseIfClauses, elseClause) => executeIfElseIfElse(ifClause, elseIfClauses, elseClause)
      case _ => assert(false)
    }

  def run(program: ChopperAst) =
    program match {
      case Statements(statements) => statements.foreach(executeStatement(_))
      case _ => throw new IllegalArgumentException("this shouldn't happen")
    }
  
}
