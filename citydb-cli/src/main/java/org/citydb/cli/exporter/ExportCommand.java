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

package org.citydb.cli.exporter;

import org.citydb.cli.command.Command;
import org.citydb.cli.exporter.citygml.CityGMLExportCommand;
import org.citydb.cli.exporter.cityjson.CityJSONExportCommand;
import picocli.CommandLine;

@CommandLine.Command(
        name = "export",
        scope = CommandLine.ScopeType.INHERIT,
        description = "Export data in a supported format.",
        synopsisSubcommandLabel = "COMMAND",
        subcommands = {
                CommandLine.HelpCommand.class,
                CityGMLExportCommand.class,
                CityJSONExportCommand.class
        })
public class ExportCommand implements Command {

    @Override
    public Integer call() {
        return CommandLine.ExitCode.OK;
    }

    @Override
    public void preprocess(CommandLine commandLine) {
        if (!commandLine.getParseResult().hasSubcommand()) {
            throw new CommandLine.ParameterException(commandLine,
                    "Missing required subcommand for the output format.");
        }
    }
}
