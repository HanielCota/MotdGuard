<div align="center">
  <img src="logo.png" alt="MotdGuard logo" width="180">

  <h1>MotdGuard</h1>

  <p>
    O plugin definitivo para deixar seu proxy <strong>Velocity</strong> mais bonito, mais controlado
    e muito mais protegido desde o primeiro ping.
  </p>

  <p>
    MOTD profissional, manutencao inteligente, rate limit contra flood e configuracao simples.
    Tudo em um plugin leve, direto e feito para servidor serio.
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

**MotdGuard** nao e so um plugin de MOTD. Ele e a primeira camada de controle do seu proxy.

Enquanto outros plugins so mudam duas linhas na lista de servidores, o MotdGuard entrega uma experiencia completa: visual forte, manutencao sem dor de cabeca e protecao real contra spam de ping. E o tipo de ferramenta que voce instala uma vez e deixa trabalhando em silencio, mantendo seu servidor com cara profissional e comportamento previsivel.

Com ele, voce altera o MOTD com MiniMessage, ativa manutencao sem reiniciar o proxy, libera bypass para staff e limita abuso por IP antes que isso vire problema.

## Por que usar?

| Motivo | Impacto |
| --- | --- |
| Visual de servidor premium | Seu servidor aparece com uma identidade mais forte e organizada na lista. |
| Controle imediato | Ative manutencao, recarregue config e ajuste mensagens sem derrubar o proxy. |
| Seguranca de verdade | Rate limit por IP ajuda a segurar flood de ping e consultas abusivas. |
| Leve e objetivo | Faz o que precisa fazer sem virar um plugin gigante e confuso. |
| Pronto para producao | Build automatizado, CodeQL, Dependabot e dependencias monitoradas. |

## Destaques

| Recurso | Descricao |
| --- | --- |
| MOTD dinamico | Transforme a primeira impressao do servidor com MiniMessage, cores e estilos modernos. |
| Modo manutencao | Feche o servidor com elegancia, mensagem customizada e controle total por comando. |
| Rate limit de ping | Segure spam e flood de consultas antes que eles virem ruido no proxy. |
| Hot reload | Ajuste tudo no `config.toml` e aplique sem reiniciar o Velocity. |
| Bypass por permissao | Staff entra quando precisa, mesmo com manutencao ativa. |
| Logs de erro | Falhas ficam registradas em `plugins/MotdGuard/errors.log` para diagnostico rapido. |

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

Configuracao simples, legivel e direta. Voce muda o comportamento do plugin sem precisar recompilar nada.

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

O MOTD usa [MiniMessage](https://docs.advntr.dev/minimessage/) para formatacao moderna de texto. Isso permite criar um visual muito mais bonito do que o padrao antigo de cores simples.

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
src/main/java/io/github/hanielcota/motdguard/
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

MotdGuard foi pensado para ser confiavel em producao. O foco e simples: proteger o proxy, manter o visual do servidor profissional e evitar que configuracao ruim ou dependencia velha vire dor de cabeca.

O projeto usa GitHub Actions para validar build, CodeQL para analise de seguranca e Dependabot para manter dependencias atualizadas. Menos improviso, mais previsibilidade.

| Area | Ferramenta |
| --- | --- |
| Build | GitHub Actions |
| Analise estatica | CodeQL |
| Dependencias | Dependabot |
| Empacotamento | Shadow Jar |

## Filosofia

MotdGuard segue uma ideia direta: plugin bom nao precisa ser pesado, confuso ou cheio de firula. Ele precisa resolver o problema, proteger o servidor e continuar funcionando sem chamar atencao.

Esse projeto existe para entregar uma camada limpa, forte e confiavel para quem quer um proxy Velocity com aparencia profissional e controle de verdade.

## Contribuicao

Contribuicoes sao bem-vindas. Para propor mudancas:

1. Faca um fork do repositorio.
2. Crie uma branch para sua alteracao.
3. Rode o build localmente.
4. Abra um pull request descrevendo o que mudou.

## Licenca

Distribuido sob a licenca MIT. Veja [LICENSE](LICENSE) para mais detalhes.
