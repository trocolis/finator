-- =============================================================================
-- V1: linha de base do esquema do identity-service.
--
-- Convenções das migrações (Flyway):
--   * Nome: V<número>__<descricao_em_snake_case>.sql, número sempre crescente.
--   * Uma migração aplicada NUNCA é editada: o Flyway guarda o checksum e
--     recusa subir se o arquivo mudar. Correções viram uma nova migração.
--   * O schema "identity" é criado pelo Flyway (quarkus.flyway.schemas).
-- =============================================================================

COMMENT ON SCHEMA identity IS 'Dados do identity-service: usuários, famílias e sessões';
