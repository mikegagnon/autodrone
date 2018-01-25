package me.michaelgagnon.chopper

import scala.collection.mutable
import ChopperParser._

case class Variable(id: String, typ: MeasurementUnitType, var value: Double)

case class InterpreterCrash(private val message: String) extends Exception(message) 

/*
sealed trait InterpreterResult
case class Error(msg: String) extends InterpreterResult
case object Success extends InterpreterResult
*/

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

  def updateVariable(variable: Variable, value: Value) =
    value match {
      case IDENTIFIER(id) => updateVariableFromIdentifier(variable, id)
      case DoubleWithType(v, measurementUnit) => ()
    }

  def newVariable(id: IDENTIFIER, value: Value) {

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
