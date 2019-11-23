package com.github.oxisto.reticulated.ast

import com.github.oxisto.reticulated.ast.expression.Call
import com.github.oxisto.reticulated.ast.expression.Expression
import com.github.oxisto.reticulated.ast.expression.Identifier
import com.github.oxisto.reticulated.ast.expression.Primary
import com.github.oxisto.reticulated.ast.simple.ExpressionStatement
import com.github.oxisto.reticulated.ast.simple.SimpleStatement
import com.github.oxisto.reticulated.ast.statement.*
import com.github.oxisto.reticulated.grammar.Python3BaseVisitor
import com.github.oxisto.reticulated.grammar.Python3Parser
import org.antlr.v4.runtime.tree.TerminalNode
import java.lang.Exception

class Visitor(val scope: Scope) : Python3BaseVisitor<Node>() {

  override fun visitFile_input(ctx: Python3Parser.File_inputContext?): FileInput {
    if (ctx == null) {
      throw EmptyContextException()
    }

    val statements = ArrayList<Statement>()

    // loop through children
    for (tree in ctx.children) {
      if (tree is TerminalNode) {
        continue
      }

      val stmt = tree.accept(StatementVisitor(this.scope)) as Statement
      statements.add(stmt)
    }

    val fileInput = FileInput(statements)

    return fileInput
  }

  override fun visitParameters(ctx: Python3Parser.ParametersContext?): Node {
    if (ctx == null) {
      throw EmptyContextException()
    }

    if (ctx.childCount == 2) {
      return ParameterList();
    }

    // second parameter is the list of (typed) arguments
    var list = ctx.getChild(1).accept(ParameterListVisitor(this.scope))

    return ParameterList(list);
  }

  override fun visitSuite(ctx: Python3Parser.SuiteContext?): Node {
    if (ctx == null) {
      throw EmptyContextException()
    }

    val list = ArrayList<Statement>()

    for (tree in ctx.children) {
      // skip commas, etc.
      if (tree is TerminalNode) {
        continue;
      }

      var stmt = tree.accept(StatementVisitor(this.scope)) as Statement
      list.add(stmt)
    }

    return Suite(list)
  }

}
