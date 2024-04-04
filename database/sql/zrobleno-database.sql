-- Database: central-api

DROP DATABASE IF EXISTS "zrobleno";

CREATE DATABASE "zrobleno"
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    LOCALE_PROVIDER = 'libc'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1
    IS_TEMPLATE = False;