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
package org.druid.sql.ast.statement;

import org.druid.sql.ast.SQLExpr;
import org.druid.sql.ast.SQLObject;
import org.druid.sql.ast.SQLStatementImpl;
import org.druid.sql.ast.statement.SQLObjectType;
import org.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SQLRevokeStatement extends SQLStatementImpl {

    private final List<SQLExpr> privileges = new ArrayList<SQLExpr>();

    private SQLObject on;
    private SQLExpr from;
    // mysql
    private org.druid.sql.ast.statement.SQLObjectType objectType;

    public SQLRevokeStatement(){

    }

    public SQLRevokeStatement(String dbType){
        super(dbType);
    }

    public SQLObject getOn() {
        return on;
    }

    public void setOn(SQLObject on) {
        this.on = on;
    }

    public SQLExpr getFrom() {
        return from;
    }

    public void setFrom(SQLExpr from) {
        this.from = from;
    }

    public List<SQLExpr> getPrivileges() {
        return privileges;
    }

    public org.druid.sql.ast.statement.SQLObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(SQLObjectType objectType) {
        this.objectType = objectType;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, on);
            acceptChild(visitor, from);
        }
        visitor.endVisit(this);
    }
}
