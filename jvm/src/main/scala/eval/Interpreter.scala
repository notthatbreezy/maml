package maml.eval

import maml.ast._
import maml.error._

import cats._
import cats.implicits._
import cats.data.Validated._
import cats.data.{NonEmptyList => NEL, _}

import scala.reflect.ClassTag

case class TmsInterpreter(directives: Seq[Directive]) {
  val fallbackDirective: Directive =
    { case (exp, res) => Invalid(NEL.of(UnhandledCase(exp, exp.kind))) }

  def instructions(expression: Expression, children: Seq[Result]) =
    directives.reduceLeft(_ orElse _).orElse(fallbackDirective)((expression, children))

  def apply(exp: Expression): Interpreted[Result] = {
    val children = exp.children.map(apply).sequence
    children.andThen({ childRes => instructions(exp, childRes) })
  }
}

object Interpreter {
  import maml.eval.directive.SourceDirectives._

  def tms(directives: Directive*) = TmsInterpreter(directives)

  def test = tms(intLiteralDirective, dblLiteralDirective, boolLiteralDirective)
}













//trait Interpreter {
//  type SrcTransform = Source => Result
//  type OpTransform = List[Result] => Result

//  var sources: Map[Expression, SrcTransform] = Map()
//  def source(src: Source)(f: SrcTransform): Unit =
//    { sources = sources updated (src, f) }
//  def lookupSrc(src: Source): Interpreted[Result] =
//    sources.get(src) match {
//      case Some(f) => Valid(f(src))
//      case None => Invalid(NEL.of(UnhandledCase(src, src.kind)))
//    }


//  var operations: Map[(Operation, MamlKind), List[Result] => Result] = Map()
//  def operation(op: Operation, kind: MamlKind)(f: OpTransform): Unit =
//    { operations = operations updated (op -> kind, f) }
//  def lookupOp(op: Operation, kind: MamlKind): Interpreted[OpTransform] =
//    operations.get(op -> kind) match {
//      case Some(func) => Valid(func)
//      case None => Invalid(NEL.of(UnhandledCase(op, kind)))
//    }

//  def evaluate(exp: Expression): Interpreted[Result] = {
//    val kind: MamlKind = exp.kind
//    exp match {
//      case op: Operation =>
//        val childrenResult = op.children.map(evaluate).sequence
//        (lookupOp(op, kind) |@| childrenResult).map({ case (f, childResults) => f(childResults) })
//      case src: Source =>
//        lookupSrc(src)
//    }
//  }
//}
