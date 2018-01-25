package me.michaelgagnon.chopper

import scala.collection.mutable
import ChopperParser._

// TODO: do we really need to store id here?
case class Variable(id: String, typ: MeasurementUnitType, var value: Double)

case class InterpreterCrash(private val message: String) extends Exception(message) 

class State {
  val variables = new mutable.HashMap[String, Variable]()
}

class Interpreter(val state: State) {


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

  def updateVariableFromLiteral(destVariable: Variable, v: Double, measurementUnit: MeasurementUnitType) {
    if (destVariable.typ == measurementUnit) {
      destVariable.value = v
    } else {
      throw new InterpreterCrash(s"Cannot assign ${v} to ${destVariable.id}, because their types do not match")
    }
  }

  def updateVariable(destVariable: Variable, value: Value) =
    value match {
      case IDENTIFIER(id) => updateVariableFromIdentifier(destVariable, id)
      case DoubleWithType(v, measurementUnit) => updateVariableFromLiteral(destVariable, v, measurementUnit.unit)
    }


  def newVariableFromIdentifier(destId: String, srcId: String) =
    state.variables.get(srcId) match {
      case Some(Variable(_, measurementUnit, v)) => state.variables(destId) = Variable(destId, measurementUnit, v)
      case None => throw new InterpreterCrash(s"Cannot assign ${srcId} to ${destId} because ${destId} is not defined")
    }

  def newVariable(destId: IDENTIFIER, value: Value) =
    value match {
      case IDENTIFIER(srcId) => newVariableFromIdentifier(destId.id, srcId)
      case DoubleWithType(v, measurementUnit) => state.variables(destId.id) = Variable(destId.id, measurementUnit.unit, v)
    }

  def executeAssignment(id: IDENTIFIER, value: Value) =
    state.variables.get(id.id) match {
      case Some(variable) => updateVariable(variable, value)
      case None => newVariable(id, value)
    }

  def executeStatement(statement: ChopperAst) =
    statement match {
      case Assignment(id, value) => executeAssignment(id, value)
      case IfElseIfElse(ifClause, elseIfClauses, elseClause) => ()
      case _ => assert(false)
    }

  def run( program: ChopperAst) =
    program match {
      case Statements(statements) => statements.foreach(executeStatement(_))
      case _ => throw new IllegalArgumentException("this shouldn't happen")
    }
  
}
