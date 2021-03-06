//
// Copyright (c) 2020, 2017 Couchbase, Inc All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package com.couchbase.lite;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;


/**
 * Configuration for opening a database.
 */
public final class DatabaseConfiguration extends AbstractDatabaseConfiguration {

    //---------------------------------------------
    // Constructors
    //---------------------------------------------

    @SuppressWarnings("ConstantConditions")
    @SuppressFBWarnings("NP_NULL_PARAM_DEREF_NONVIRTUAL")
    public DatabaseConfiguration() { this(null, false); }

    public DatabaseConfiguration(@NonNull DatabaseConfiguration config) { this(config, false); }

    DatabaseConfiguration(@Nullable DatabaseConfiguration config, boolean readOnly) { super(config, readOnly); }

    //---------------------------------------------
    // API - public methods
    //---------------------------------------------

    /**
     * Set the path to the directory to store the database in. If the directory doesn't already
     * exist it will be created when the database is opened.
     *
     * @param directory the directory
     * @return The self object.
     */
    @NonNull
    @Override
    public DatabaseConfiguration setDirectory(@NonNull String directory) {
        super.setDirectory(directory);
        return this;
    }

    //---------------------------------------------
    // protected methods
    //---------------------------------------------

    @Override
    protected DatabaseConfiguration getDatabaseConfiguration() { return this; }
}
