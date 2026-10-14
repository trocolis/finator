-- =============================================================================
-- Cria um database e um usuário exclusivos para cada serviço (ADR-0002).
--
-- Rode como administrador do PostgreSQL, passando as senhas como variáveis
-- do psql (assim elas não ficam gravadas em nenhum arquivo):
--
--   psql -h <host> -U postgres \
--        -v identity_password='...' \
--        -v ledger_password='...' \
--        -f deploy/postgres/create-databases.sql
--
-- O script é idempotente: rodar de novo não dá erro nem duplica nada.
-- =============================================================================

\set ON_ERROR_STOP on

-- O PostgreSQL não tem "CREATE ROLE IF NOT EXISTS" nem "CREATE DATABASE IF NOT
-- EXISTS". O truque: um SELECT monta o comando só se o objeto não existir, e o
-- \gexec do psql executa cada linha do resultado como um novo comando SQL.
-- format() com %I e %L escapa identificadores e literais, evitando SQL injection.

-- ---------------------------------------------------------------- identity ---
SELECT format('CREATE ROLE %I LOGIN PASSWORD %L', 'finator_identity', :'identity_password')
WHERE NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'finator_identity')
\gexec

SELECT 'CREATE DATABASE finator_identity OWNER finator_identity'
WHERE NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'finator_identity')
\gexec

-- Por padrão qualquer usuário pode se conectar a qualquer database; aqui não.
REVOKE ALL ON DATABASE finator_identity FROM PUBLIC;

-- ------------------------------------------------------------------ ledger ---
SELECT format('CREATE ROLE %I LOGIN PASSWORD %L', 'finator_ledger', :'ledger_password')
WHERE NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'finator_ledger')
\gexec

SELECT 'CREATE DATABASE finator_ledger OWNER finator_ledger'
WHERE NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'finator_ledger')
\gexec

REVOKE ALL ON DATABASE finator_ledger FROM PUBLIC;
