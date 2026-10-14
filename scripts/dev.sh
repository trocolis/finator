#!/usr/bin/env bash
# =============================================================================
# Sobe um serviço em modo de desenvolvimento (live reload) com as variáveis
# do arquivo .env da raiz do projeto.
#
# Uso: scripts/dev.sh <identity|ledger>
#
# Cada serviço usa uma porta HTTP e uma porta de debug diferentes, assim os
# dois podem rodar ao mesmo tempo em terminais separados.
# =============================================================================
set -euo pipefail

# Raiz do projeto: a pasta acima deste script, independente de onde ele é chamado
root_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

case "${1:-}" in
    identity) module=identity-service; debug_port=5005 ;;
    ledger)   module=ledger-service;   debug_port=5006 ;;
    *)
        echo "Uso: $0 <identity|ledger>" >&2
        exit 1
        ;;
esac

if [[ ! -f "$root_dir/.env" ]]; then
    echo "Arquivo .env não encontrado. Crie com: cp .env.example .env" >&2
    exit 1
fi

# "set -a" exporta automaticamente toda variável definida ao ler o .env
set -a
# shellcheck source=/dev/null
source "$root_dir/.env"
set +a

cd "$root_dir"
# -pl escolhe o módulo; -am compila antes os módulos dos quais ele depende (finator-commons)
exec ./mvnw quarkus:dev -pl "$module" -am -Ddebug="$debug_port"
