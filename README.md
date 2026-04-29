<div align="center">
  <img src="logo.png" alt="MotdGuard logo" width="180">

  <h1>MotdGuard</h1>

  <p>
    Um plugin moderno para <strong>Velocity</strong> que controla o MOTD, protege contra spam de ping
    e gerencia modo de manutencao com recarregamento em tempo real.
  </p>

  <p>
    <a href="https://github.com/HanielCota/MotdGuard/actions/workflows/build.yml">
      <img alt="Build" src="https://img.shields.io/github/actions/workflow/status/HanielCota/MotdGuard/build.yml?branch=main&style=for-the-badge&label=build">
    </a>
    <a href="https://github.com/HanielCota/MotdGuard/security/code-scanning">
      <img alt="CodeQL" src="https://img.shields.io/github/actions/workflow/status/HanielCota/MotdGuard/codeql.yml?branch=main&style=for-the-badge&label=codeql">
    </a>
    <a href="LICENSE">
      <img alt="License" src="https://img.shields.io/github/license/HanielCota/MotdGuard?style=for-the-badge">
    </a>
    <img alt="Java" src="https://img.shields.io/badge/Java-21-f58220?style=for-the-badge">
    <img alt="Velocity" src="https://img.shields.io/badge/Velocity-3.5%2B-1f6feb?style=for-the-badge">
  </p>
</div>

---

## Visao Geral

**MotdGuard** foi criado para servidores Velocity que precisam de uma camada simples e confiavel para controlar a primeira impressao do servidor e reduzir abuso no endpoint de ping.

Com ele, voce pode alterar o MOTD com MiniMessage, ativar manutencao sem reiniciar o proxy, permitir bypass por permissao e limitar spam de ping por IP.

## Destaques

| Recurso | Descricao |
| --- | --- |
| MOTD dinamico | Personalize as linhas exibidas na lista de servidores com suporte a MiniMessage. |
| Modo manutencao | Bloqueie entradas temporariamente com mensagem customizada. |
| Rate limit de ping | Reduza spam e flood de consultas ao proxy por IP. |
| Hot reload | Recarregue o `config.toml` sem reiniciar o Velocity. |
| Bypass por permissao | Permita que staff entre mesmo durante manutencao. |
| Logs de erro | Registre falhas em `plugins/MotdGuard/errors.log` para diagnostico. |

## Requisitos

| Item | Versao |
| --- | --- |
| Java | 21+ |
| Velocity | 3.5.0+ |
| Gradle | Wrapper incluso no projeto |

## Instalacao

1. Baixe o arquivo `.jar` mais recente em [GitHub Releases](https://github.com/HanielCota/MotdGuard/releases).
2. Coloque o arquivo em `plugins/` no seu proxy Velocity.
3. Reinicie o proxy para gerar a configuracao inicial.
4. Edite `plugins/MotdGuard/config.toml`.
5. Use `/motdguard reload` para aplicar alteracoes sem reiniciar.

## Configuracao

Arquivo principal:

```text
plugins/MotdGuard/config.toml
```

Exemplo:

```toml
[motd]
line1 = "<#f58220><bold>MeuServidor</bold>"
line2 = "<#ffffff>Protegido por <#f58220>MotdGuard"

[maintenance]
enabled = false
kick-message = "<red>Servidor em manutencao. Volte em breve!"

[ratelimit]
enabled = true
max-pings-per-minute = 60
block-message = "Muitas requisicoes. Aguarde."

[messages]
reload-success = "&aConfiguracao recarregada com sucesso."
reload-failure = "&cFalha ao recarregar a configuracao. Verifique o console."
maintenance-enabled = "&aModo manutencao ativado."
maintenance-disabled = "&aModo manutencao desativado."
maintenance-toggled = "&aModo manutencao {status}."
help-header = "&aComandos do MotdGuard:"
help-reload = "&e/motdguard reload - Recarrega a configuracao"
help-maintenance = "&e/motdguard maintenance - Alterna o modo manutencao"
help-maintenance-on = "&e/motdguard maintenance on - Ativa a manutencao"
help-maintenance-off = "&e/motdguard maintenance off - Desativa a manutencao"
```

## MiniMessage

O MOTD usa [MiniMessage](https://docs.advntr.dev/minimessage/) para formatacao moderna de texto.

| Sintaxe | Resultado |
| --- | --- |
| `<green>Online` | Cor nomeada |
| `<#f58220>MotdGuard` | Cor hexadecimal |
| `<bold>Texto</bold>` | Texto em negrito |
| `<gradient:#f58220:#ffffff>Servidor</gradient>` | Gradiente |
| `<hover:show_text:'Info'>Passe o mouse</hover>` | Texto com hover |

## Comandos

| Comando | Descricao | Permissao |
| --- | --- | --- |
| `/motdguard` | Mostra o menu de ajuda | `motdguard.admin` |
| `/mg` | Alias principal | `motdguard.admin` |
| `/motdguard reload` | Recarrega a configuracao | `motdguard.admin` |
| `/motdguard maintenance` | Alterna o modo manutencao | `motdguard.admin` |
| `/motdguard maintenance on` | Ativa o modo manutencao | `motdguard.admin` |
| `/motdguard maintenance off` | Desativa o modo manutencao | `motdguard.admin` |
| `/mg m` | Alias para manutencao | `motdguard.admin` |

## Permissoes

| Permissao | Descricao | Padrao |
| --- | --- | --- |
| `motdguard.admin` | Acesso aos comandos administrativos | `op` |
| `motdguard.bypass` | Permite entrar durante manutencao | `false` |

## Build Local

Use o Gradle Wrapper incluido no repositorio.

```bash
./gradlew build
```

Build sem SpotBugs:

```bash
./gradlew build -x spotbugsMain -x spotbugsTest
```

Gerar o JAR final:

```bash
./gradlew shadowJar
```

O artefato compilado fica em:

```text
build/libs/motdguard-1.0.0.jar
```

## Estrutura

```text
src/main/java/io/github/hanielcot/motdguard/
├── MotdGuardPlugin.java
├── command/
│   └── MotdGuardCommand.java
├── config/
│   ├── ConfigData.java
│   ├── ConfigManager.java
│   ├── MaintenanceConfig.java
│   ├── MessagesConfig.java
│   ├── MotdConfig.java
│   └── RateLimitConfig.java
├── exception/
│   └── PluginExceptionHandler.java
├── listener/
│   ├── LoginListener.java
│   └── PingListener.java
└── service/
    ├── MaintenanceService.java
    ├── MotdService.java
    └── RateLimitService.java
```

## Qualidade e Seguranca

O projeto usa GitHub Actions para validar build, CodeQL para analise de seguranca e Dependabot para manter dependencias atualizadas.

| Area | Ferramenta |
| --- | --- |
| Build | GitHub Actions |
| Analise estatica | CodeQL |
| Dependencias | Dependabot |
| Empacotamento | Shadow Jar |

## Contribuicao

Contribuicoes sao bem-vindas. Para propor mudancas:

1. Faca um fork do repositorio.
2. Crie uma branch para sua alteracao.
3. Rode o build localmente.
4. Abra um pull request descrevendo o que mudou.

## Licenca

Distribuido sob a licenca MIT. Veja [LICENSE](LICENSE) para mais detalhes.
