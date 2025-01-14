/*
 * citydb-tool - Command-line tool for the 3D City Database
 * https://www.3dcitydb.org/
 *
 * Copyright 2022-2023
 * Virtual City Systems, Germany
 * https://vc.systems/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.citydb.io.reader;

public class ReadOptions {
    private boolean failFast;
    private int numberOfThreads;
    private String encoding;
    private boolean computeEnvelopes;
    private Object formatOptions;

    private ReadOptions() {
    }

    public static ReadOptions defaults() {
        return new ReadOptions();
    }

    public boolean isFailFast() {
        return failFast;
    }

    public ReadOptions setFailFast(boolean failFast) {
        this.failFast = failFast;
        return this;
    }

    public int getNumberOfThreads() {
        return numberOfThreads;
    }

    public ReadOptions setNumberOfThreads(int numberOfThreads) {
        if (numberOfThreads > 0) {
            this.numberOfThreads = numberOfThreads;
        }

        return this;
    }

    public String getEncoding() {
        return encoding;
    }

    public ReadOptions setEncoding(String encoding) {
        this.encoding = encoding;
        return this;
    }

    public boolean isComputeEnvelopes() {
        return computeEnvelopes;
    }

    public ReadOptions setComputeEnvelopes(boolean computeEnvelopes) {
        this.computeEnvelopes = computeEnvelopes;
        return this;
    }

    public Object getFormatOptions() {
        return formatOptions;
    }

    public ReadOptions setFormatOptions(Object formatOptions) {
        this.formatOptions = formatOptions;
        return this;
    }
}
