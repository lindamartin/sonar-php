/*
 * SonarQube PHP Plugin
 * Copyright (C) 2010 SonarSource and Akram Ben Aissi
 * sonarqube@googlegroups.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.php.tree.symbols;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.sonar.plugins.php.api.symbols.Symbol;
import org.sonar.plugins.php.api.tree.CompilationUnitTree;
import org.sonar.plugins.php.api.tree.Tree;
import org.sonar.plugins.php.api.tree.declaration.ClassDeclarationTree;
import org.sonar.plugins.php.api.tree.declaration.ClassPropertyDeclarationTree;
import org.sonar.plugins.php.api.tree.declaration.ConstantDeclarationTree;
import org.sonar.plugins.php.api.tree.declaration.FunctionDeclarationTree;
import org.sonar.plugins.php.api.tree.declaration.MethodDeclarationTree;
import org.sonar.plugins.php.api.tree.declaration.ParameterTree;
import org.sonar.plugins.php.api.tree.declaration.VariableDeclarationTree;
import org.sonar.plugins.php.api.tree.expression.FunctionExpressionTree;
import org.sonar.plugins.php.api.tree.expression.IdentifierTree;
import org.sonar.plugins.php.api.tree.expression.LexicalVariablesTree;
import org.sonar.plugins.php.api.tree.expression.VariableIdentifierTree;
import org.sonar.plugins.php.api.tree.expression.VariableTree;
import org.sonar.plugins.php.api.tree.statement.GlobalStatementTree;
import org.sonar.plugins.php.api.visitors.PHPVisitorCheck;

import java.util.List;

public class SymbolVisitor extends PHPVisitorCheck {

  private SymbolTableImpl symbolTable;
  private Scope currentScope;
  private Scope globalScope;

  public SymbolVisitor(SymbolTableImpl symbolTable) {
    this.symbolTable = symbolTable;
    this.currentScope = null;
    this.globalScope = null;
  }

  @Override
  public void visitCompilationUnit(CompilationUnitTree tree) {
    newScope(tree);
    globalScope = currentScope;
    super.visitCompilationUnit(tree);
  }

  @Override
  public void visitFunctionDeclaration(FunctionDeclarationTree tree) {
    createSymbol(tree.name(), Symbol.Kind.FUNCTION);
    newScope(tree);
    super.visitFunctionDeclaration(tree);
    leaveScope();
  }

  @Override
  public void visitFunctionExpression(FunctionExpressionTree tree) {
    newScope(tree);
    super.visitFunctionExpression(tree);
    leaveScope();
  }

  @Override
  public void visitMethodDeclaration(MethodDeclarationTree tree) {
    createSymbol(tree.name(), Symbol.Kind.FUNCTION);
    newScope(tree);
    super.visitMethodDeclaration(tree);
    leaveScope();
  }

  @Override
  public void visitClassDeclaration(ClassDeclarationTree tree) {
    createSymbol(tree.name(), Symbol.Kind.CLASS);
    newScope(tree);
    super.visitClassDeclaration(tree);
    leaveScope();
  }

  @Override
  public void visitClassPropertyDeclaration(ClassPropertyDeclarationTree tree) {
    for (VariableDeclarationTree field : tree.declarations()) {
      createSymbol(field.identifier(), Symbol.Kind.FIELD).addModifiers(tree.modifierTokens());
    }
  }

  @Override
  public void visitConstDeclaration(ConstantDeclarationTree tree) {
    for (VariableDeclarationTree constant : tree.declarations()) {
      createSymbol(constant.identifier(), Symbol.Kind.VARIABLE).addModifiers(Lists.newArrayList(tree.constToken()));
    }
  }

  @Override
  public void visitVariableIdentifier(VariableIdentifierTree tree) {
    createSymbol(tree.variableExpression(), Symbol.Kind.VARIABLE);
  }

  @Override
  public void visitParameter(ParameterTree tree) {
    createSymbol(tree.variableIdentifier().variableExpression(), Symbol.Kind.PARAMETER);
    // do not scan the children to not pass through variableIdentifier
  }

  @Override
  public void visitGlobalStatement(GlobalStatementTree tree) {
    useSymbolsFromOuterScope(tree.variables(), globalScope);
  }

  @Override
  public void visitLexicalVariables(LexicalVariablesTree tree) {
    useSymbolsFromOuterScope(tree.variables(), currentScope.outer());
  }

  private void useSymbolsFromOuterScope(List<VariableTree> variables, Scope outerScope) {
    for (VariableTree variable : variables) {

      IdentifierTree identifier = null;
      if (variable.is(Tree.Kind.VARIABLE_IDENTIFIER)) {
        identifier = (IdentifierTree) variable.variableExpression();

      } else if (variable.is(Tree.Kind.REFERENCE_VARIABLE) && variable.variableExpression().is(Tree.Kind.VARIABLE_IDENTIFIER)) {
        identifier = ((VariableIdentifierTree) variable.variableExpression()).variableExpression();
      }
      // Other cases are not supported, e.g: variable variables $$a

      if (identifier != null) {
        Symbol symbol = outerScope.getSymbol(identifier.text(), Symbol.Kind.VARIABLE);

        if (symbol != null) {
          currentScope.addSymbol(symbol);
        } else {
          createSymbol(identifier, Symbol.Kind.VARIABLE);
        }

      }

    }

  }

  private void leaveScope() {
    Preconditions.checkState(currentScope != null, "Current scope should never be null when calling method \"leaveScope\"");
    currentScope = currentScope.outer();
  }

  private void newScope(Tree tree) {
    currentScope = new Scope(currentScope, tree);
    symbolTable.addScope(currentScope);
  }

  private Symbol createSymbol(IdentifierTree identifier, Symbol.Kind kind) {
    Symbol symbol = currentScope.getSymbol(identifier.text(), kind);

    if (symbol == null) {
      symbol = symbolTable.declareSymbol(identifier, kind, currentScope);

    } else {
      // fixme: handle usages
    }
    return symbol;
  }

}
