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
package org.druid.sql.dialect.postgresql.ast.stmt;

import org.druid.sql.ast.SQLExpr;
import org.druid.sql.ast.statement.SQLSelectQuery;
import org.druid.sql.dialect.postgresql.ast.PGSQLObjectImpl;
import org.druid.sql.dialect.postgresql.visitor.PGASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class PGValuesQuery extends PGSQLObjectImpl implements SQLSelectQuery {
    private boolean          bracket  = false;

    private List<SQLExpr> values = new ArrayList<SQLExpr>();

    public List<SQLExpr> getValues() {
        return values;
    }

    @Override
    public void accept0(PGASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, values);
        }
        visitor.endVisit(this);
    }

    @Override
    public boolean isBracket() {
        return bracket;
    }

    @Override
    public void setBracket(boolean bracket) {
        this.bracket = bracket;
    }
}
