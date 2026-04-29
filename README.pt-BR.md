<div align="center">
  <img src=".github/assets/logo.png" alt="Logo do MotdGuard" width="180">

  <h1>MotdGuard</h1>

  <p>
    O plugin definitivo para deixar seu proxy <strong>Velocity</strong> mais bonito, mais controlado
    e muito mais protegido desde o primeiro ping.
  </p>

  <p>
    MOTD profissional, manutenção inteligente, rate limit contra flood e configuração simples.
    Tudo em um plugin leve, direto e feito para servidor sério.
  </p>

  <p>
    <a href="README.md">
      <img alt="Read in English" src="https://img.shields.io/badge/Read%20in-English-1f6feb?style=for-the-badge">
    </a>
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

## Visão Geral

**MotdGuard** não é só um plugin de MOTD. Ele é a primeira camada de controle do seu proxy.

Enquanto outros plugins só mudam duas linhas na lista de servidores, o MotdGuard entrega uma experiência completa: visual forte, manutenção sem dor de cabeça e proteção real contra spam de ping. É o tipo de ferramenta que você instala uma vez e deixa trabalhando em silêncio, mantendo seu servidor com cara profissional e comportamento previsível.

Com ele, você altera o MOTD com MiniMessage, ativa manutenção sem reiniciar o proxy, libera bypass para staff e limita abuso por IP antes que isso vire problema.

## Por que usar?

| Motivo | Impacto |
| --- | --- |
| Visual de servidor premium | Seu servidor aparece com uma identidade mais forte e organizada na lista. |
| Controle imediato | Ative manutenção, recarregue a configuração e ajuste mensagens sem derrubar o proxy. |
| Segurança de verdade | Rate limit por IP ajuda a segurar flood de ping e consultas abusivas. |
| Leve e objetivo | Faz o que precisa fazer sem virar um plugin gigante e confuso. |
| Pronto para produção | Build automatizado, CodeQL, Dependabot e dependências monitoradas. |

## Destaques

| Recurso | Descrição |
| --- | --- |
| MOTD dinâmico | Transforme a primeira impressão do servidor com MiniMessage, cores e estilos modernos. |
| Modo manutenção | Feche o servidor com elegância, mensagem customizada e controle total por comando. |
| Rate limit de ping | Segure spam e flood de consultas antes que eles virem ruído no proxy. |
| Hot reload | Ajuste tudo no `config.toml` e aplique sem reiniciar o Velocity. |
| Bypass por permissão | Staff entra quando precisa, mesmo com manutenção ativa. |
| Logs de erro | Falhas ficam registradas em `plugins/MotdGuard/errors.log` para diagnóstico rápido. |

## Requisitos

| Item | Versão |
| --- | --- |
| Java | 21+ |
| Velocity | 3.5.0+ |
| Gradle | Wrapper incluso no projeto |

## Instalação

1. Baixe o arquivo `.jar` mais recente em [GitHub Releases](https://github.com/HanielCota/MotdGuard/releases).
2. Coloque o arquivo em `plugins/` no seu proxy Velocity.
3. Reinicie o proxy para gerar a configuração inicial.
4. Edite `plugins/MotdGuard/config.toml`.
5. Use `/motdguard reload` para aplicar alterações sem reiniciar.

## Configuração

Configuração simples, legível e direta. Você muda o comportamento do plugin sem precisar recompilar nada.

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
kick-message = "<red>Servidor em manutenção. Volte em breve!"

[ratelimit]
enabled = true
max-pings-per-minute = 60
block-message = "Muitas requisições. Aguarde."

[messages]
reload-success = "&aConfiguração recarregada com sucesso."
reload-failure = "&cFalha ao recarregar a configuração. Verifique o console."
maintenance-enabled = "&aModo manutenção ativado."
maintenance-disabled = "&aModo manutenção desativado."
maintenance-toggled = "&aModo manutenção {status}."
help-header = "&aComandos do MotdGuard:"
help-reload = "&e/motdguard reload - Recarrega a configuração"
help-maintenance = "&e/motdguard maintenance - Alterna o modo manutenção"
help-maintenance-on = "&e/motdguard maintenance on - Ativa a manutenção"
help-maintenance-off = "&e/motdguard maintenance off - Desativa a manutenção"
```

## MiniMessage

O MOTD usa [MiniMessage](https://docs.advntr.dev/minimessage/) para formatação moderna de texto. Isso permite criar um visual muito mais bonito do que o padrão antigo de cores simples.

| Sintaxe | Resultado |
| --- | --- |
| `<green>Online` | Cor nomeada |
| `<#f58220>MotdGuard` | Cor hexadecimal |
| `<bold>Texto</bold>` | Texto em negrito |
| `<gradient:#f58220:#ffffff>Servidor</gradient>` | Gradiente |
| `<hover:show_text:'Info'>Passe o mouse</hover>` | Texto com hover |

## Comandos

| Comando | Descrição | Permissão |
| --- | --- | --- |
| `/motdguard` | Mostra o menu de ajuda | `motdguard.admin` |
| `/mg` | Alias principal | `motdguard.admin` |
| `/motdguard reload` | Recarrega a configuração | `motdguard.admin` |
| `/motdguard maintenance` | Alterna o modo manutenção | `motdguard.admin` |
| `/motdguard maintenance on` | Ativa o modo manutenção | `motdguard.admin` |
| `/motdguard maintenance off` | Desativa o modo manutenção | `motdguard.admin` |
| `/mg m` | Alias para manutenção | `motdguard.admin` |

## Permissões

| Permissão | Descrição | Padrão |
| --- | --- | --- |
| `motdguard.admin` | Acesso aos comandos administrativos | `op` |
| `motdguard.bypass` | Permite entrar durante manutenção | `false` |

## Build Local

Use o Gradle Wrapper incluído no repositório.

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

## Qualidade e Segurança

MotdGuard foi pensado para ser confiável em produção. O foco é simples: proteger o proxy, manter o visual do servidor profissional e evitar que configuração ruim ou dependência velha vire dor de cabeça.

O projeto usa GitHub Actions para validar build, CodeQL para análise de segurança e Dependabot para manter dependências atualizadas. Menos improviso, mais previsibilidade.

| Área | Ferramenta |
| --- | --- |
| Build | GitHub Actions |
| Análise estática | CodeQL |
| Dependências | Dependabot |
| Empacotamento | Shadow Jar |

## Filosofia

MotdGuard segue uma ideia direta: plugin bom não precisa ser pesado, confuso ou cheio de firula. Ele precisa resolver o problema, proteger o servidor e continuar funcionando sem chamar atenção.

Este projeto existe para entregar uma camada limpa, forte e confiável para quem quer um proxy Velocity com aparência profissional e controle de verdade.

## Contribuição

Contribuições são bem-vindas. Para propor mudanças:

1. Faça um fork do repositório.
2. Crie uma branch para sua alteração.
3. Rode o build localmente.
4. Abra um pull request descrevendo o que mudou.

## Licença

Distribuído sob a licença MIT. Veja [LICENSE](LICENSE) para mais detalhes.
