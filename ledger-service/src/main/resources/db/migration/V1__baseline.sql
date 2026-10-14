-- =============================================================================
-- V1: linha de base do esquema do ledger-service.
--
-- Convenções das migrações (Flyway):
--   * Nome: V<número>__<descricao_em_snake_case>.sql, número sempre crescente.
--   * Uma migração aplicada NUNCA é editada: o Flyway guarda o checksum e
--     recusa subir se o arquivo mudar. Correções viram uma nova migração.
--   * O schema "ledger" é criado pelo Flyway (quarkus.flyway.schemas).
-- =============================================================================

COMMENT ON SCHEMA ledger IS 'Dados do ledger-service: contas, títulos, parcelas e efetivações';
