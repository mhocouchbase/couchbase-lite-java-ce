//
// Copyright (c) 2019 Couchbase, Inc All rights reserved.
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


public final class ReplicatorConfiguration extends AbstractReplicatorConfiguration {
    public ReplicatorConfiguration(@NonNull ReplicatorConfiguration config) {
        super(config);
    }

    public ReplicatorConfiguration(@NonNull Database database, @NonNull Endpoint target) {
        super(database, target);
    }

    @Override
    ReplicatorConfiguration getReplicatorConfiguration() { return this; }

    @Override
    Database getTargetDatabase() { return null; }
}