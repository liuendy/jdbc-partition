/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.the.force.thirdparty.druid.sql.dialect.db2.parser;

import org.the.force.thirdparty.druid.sql.ast.SQLOrderBy;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLSetQuantifier;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQuery;
import org.the.force.thirdparty.druid.sql.dialect.db2.ast.stmt.DB2SelectQueryBlock;
import org.the.force.thirdparty.druid.sql.parser.ParserException;
import org.the.force.thirdparty.druid.sql.parser.SQLExprParser;
import org.the.force.thirdparty.druid.sql.parser.SQLSelectParser;
import org.the.force.thirdparty.druid.sql.parser.Token;

public class DB2SelectParser extends SQLSelectParser {

    public DB2SelectParser(SQLExprParser exprParser){
        super(exprParser);
    }

    public DB2SelectParser(String sql){
        this(new DB2ExprParser(sql));
    }

    protected SQLExprParser createExprParser() {
        return new DB2ExprParser(lexer);
    }

    @Override
    public SQLSelectQuery query() {
        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();

            SQLSelectQuery select = query();
            accept(Token.RPAREN);

            return queryRest(select);
        }

        accept(Token.SELECT);

        if (lexer.token() == Token.COMMENT) {
            lexer.nextToken();
        }

        DB2SelectQueryBlock queryBlock = new DB2SelectQueryBlock();

        if (lexer.token() == Token.DISTINCT) {
            queryBlock.setDistionOption(SQLSetQuantifier.DISTINCT);
            lexer.nextToken();
        } else if (lexer.token() == Token.UNIQUE) {
            queryBlock.setDistionOption(SQLSetQuantifier.UNIQUE);
            lexer.nextToken();
        } else if (lexer.token() == Token.ALL) {
            queryBlock.setDistionOption(SQLSetQuantifier.ALL);
            lexer.nextToken();
        }

        parseSelectList(queryBlock);

        parseFrom(queryBlock);

        parseWhere(queryBlock);

        parseHierachical(queryBlock);

        parseGroupBy(queryBlock);
        
        if (lexer.token() == Token.ORDER) {
            SQLOrderBy orderBy = parseOrderBy();
            queryBlock.setOrderBy(orderBy);
        }


        for (;;) {
            if (lexer.token() == Token.FETCH) {
                lexer.nextToken();
                accept(Token.FIRST);
                SQLExpr first = this.exprParser.primary();
                queryBlock.setFirst(first);
                if (identifierEquals("ROW") || identifierEquals("ROWS")) {
                    lexer.nextToken();
                }
                accept(Token.ONLY);
                continue;
            }
            
            if (lexer.token() == Token.WITH) {
                lexer.nextToken();
                if (identifierEquals("RR")) {
                    queryBlock.setIsolation(DB2SelectQueryBlock.Isolation.RR);
                } else if (identifierEquals("RS")) {
                    queryBlock.setIsolation(DB2SelectQueryBlock.Isolation.RS);
                } else if (identifierEquals("CS")) {
                    queryBlock.setIsolation(DB2SelectQueryBlock.Isolation.CS);
                } else if (identifierEquals("UR")) {
                    queryBlock.setIsolation(DB2SelectQueryBlock.Isolation.UR);
                } else {
                    throw new ParserException("TODO");
                }
                lexer.nextToken();
                continue;
            }
            
            if (lexer.token() == Token.FOR) {
                lexer.nextToken();
                
                if (lexer.token() == Token.UPDATE) {
                    queryBlock.setForUpdate(true);
                    lexer.nextToken();
                } else {
                    acceptIdentifier("READ");
                    accept(Token.ONLY);
                    queryBlock.setForReadOnly(true);
                }
            }
            
            if (lexer.token() == Token.OPTIMIZE) {
                lexer.nextToken();
                accept(Token.FOR);
                
                queryBlock.setOptimizeFor(this.expr());
                if (identifierEquals("ROW")) {
                    lexer.nextToken();
                } else {
                    acceptIdentifier("ROWS");
                }
            }
            
            break;
        }

        return queryRest(queryBlock);
    }
}
