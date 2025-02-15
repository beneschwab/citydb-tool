-- 3D City Database - The Open Source CityGML Database
-- https://www.3dcitydb.org/
--
-- Copyright 2013 - 2021
-- Chair of Geoinformatics
-- Technical University of Munich, Germany
-- https://www.lrg.tum.de/gis/
--
-- The 3D City Database is jointly developed with the following
-- cooperation partners:
--
-- Virtual City Systems, Berlin <https://vc.systems/>
-- M.O.S.S. Computer Grafik Systeme GmbH, Taufkirchen <http://www.moss.de/>
--
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
--     http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--

\pset footer off
SET client_min_messages TO WARNING;
\set ON_ERROR_STOP ON

\set SCHEMA_NAME :schema_name

\echo 'Creating 3DCityDB schema "':SCHEMA_NAME'" ...'

--// create schema
CREATE SCHEMA :"SCHEMA_NAME";

--// set search_path for this session
SELECT current_setting('search_path') AS current_path;
\gset
SET search_path TO :"SCHEMA_NAME", :current_path;

--// check if the PostGIS extension and the citydb_pkg package are available
SELECT postgis_version();
SELECT version as citydb_version from citydb_pkg.citydb_version();

--// create TABLES, SEQUENCES, CONSTRAINTS, INDEXES
\echo
\echo 'Setting up database schema ...'
\ir ../../SCHEMA/SCHEMA.sql

--// fill metadata tables
\ir ../../SCHEMA/METADATA/NAMESPACE_INSTANCES.sql
\ir ../../SCHEMA/METADATA/OBJECTCLASS_INSTANCES.sql
\ir ../../SCHEMA/METADATA/DATATYPE_INSTANCES.sql
\ir ../../SCHEMA/METADATA/AGGREGATION_INFO_INSTANCES.sql

--// fill codelist tables
\ir ../../SCHEMA/CODELIST/CODELIST_INSTANCES.sql
\ir ../../SCHEMA/CODELIST/CODELIST_ENTRY_INSTANCES.sql

\echo
\echo 'Created 3DCityDB schema "':SCHEMA_NAME'".'

\echo 'Setting spatial reference system for schema "':SCHEMA_NAME'" (will be the same as for schema "citydb") ...'
\set SCHEMA_NAME_QUOTED '\'':SCHEMA_NAME'\''
SELECT citydb_pkg.change_schema_srid(database_srs.srid, database_srs.srs_name, 0, :SCHEMA_NAME_QUOTED) FROM citydb.database_srs LIMIT 1;
\echo 'Done'