<div align="center">

<img src=".github/assets/logo.png" alt="Logo do MotdGuard" width="170">

# MotdGuard

**A primeira camada de controle do seu proxy Velocity.**
MOTD profissional · modo manutenção inteligente · proteção contra flood de ping — desde o primeiro ping ao servidor.

<br>

[![Build](https://img.shields.io/github/actions/workflow/status/HanielCota/MotdGuard/build.yml?branch=main&style=for-the-badge&labelColor=0d1117&color=f58220&label=Build&logo=githubactions&logoColor=f58220)](https://github.com/HanielCota/MotdGuard/actions/workflows/build.yml)
[![CodeQL](https://img.shields.io/github/actions/workflow/status/HanielCota/MotdGuard/codeql.yml?branch=main&style=for-the-badge&labelColor=0d1117&color=f58220&label=CodeQL&logo=github&logoColor=f58220)](https://github.com/HanielCota/MotdGuard/security/code-scanning)
[![License](https://img.shields.io/github/license/HanielCota/MotdGuard?style=for-the-badge&labelColor=0d1117&color=f58220&label=Licen%C3%A7a)](LICENSE)

[![Java](https://img.shields.io/badge/Java-21-f58220?style=for-the-badge&labelColor=0d1117&logo=openjdk&logoColor=f58220)](https://adoptium.net/)
[![Velocity](https://img.shields.io/badge/Velocity-3.5%2B-f58220?style=for-the-badge&labelColor=0d1117)](https://papermc.io/software/velocity)
[![MiniMessage](https://img.shields.io/badge/MiniMessage-pronto-f58220?style=for-the-badge&labelColor=0d1117)](https://docs.advntr.dev/minimessage/)

<br>

**🌐 Idioma:** [English](README.md) · **Português**

</div>

---

## 🛡️ Visão Geral

**MotdGuard** não é só mais um plugin de MOTD — é a primeira camada de controle do seu proxy Velocity.

Enquanto muitos plugins apenas mudam duas linhas na lista de servidores, o MotdGuard entrega uma experiência completa: apresentação forte, modo manutenção sem dor de cabeça e proteção real contra spam de ping. É o tipo de ferramenta que você instala uma vez e deixa trabalhando silenciosamente em segundo plano — mantendo o servidor profissional, previsível e protegido.

Personalize o MOTD com MiniMessage, ative a manutenção sem reiniciar o proxy, permita o bypass da staff e limite o tráfego abusivo de ping por IP antes que vire problema.

> [!TIP]
> Instale uma vez, configure em minutos e use `/motdguard reload` para aplicar mudanças ao vivo — sem reiniciar o proxy.

---

## ✨ Recursos

| | Recurso | Descrição |
| :-: | --- | --- |
| 🎨 | **MOTD dinâmico** | Crie uma primeira impressão forte com MiniMessage — cores, gradientes e estilos modernos de texto. |
| 🔧 | **Modo manutenção** | Feche o servidor de forma limpa, com mensagem personalizada e controle total por comandos. |
| 🚦 | **Rate limit de ping** | Segure spam e consultas abusivas de status antes que virem ruído no proxy. |
| ♻️ | **Hot reload** | Atualize o `config.toml` e aplique as mudanças sem reiniciar o Velocity. |
| 🔑 | **Bypass por permissão** | A staff continua entrando mesmo com a manutenção ativa. |
| ⏱️ | **Cooldown de comando** | Limita comandos administrativos para evitar spam acidental. |
| 📝 | **Logs de erro** | Falhas são gravadas em `plugins/MotdGuard/errors.log` para diagnóstico rápido. |

---

## 📋 Requisitos

| Item | Versão |
| --- | --- |
| ☕ **Java** | `21+` |
| 🚀 **Velocity** | `3.5.0+` |
| 🐘 **Gradle** | Wrapper já incluído no projeto |

---

## 📦 Instalação

1. Baixe o `.jar` mais recente em [**GitHub Releases**](https://github.com/HanielCota/MotdGuard/releases).
2. Coloque o arquivo na pasta `plugins/` do seu proxy Velocity.
3. Reinicie o proxy para gerar a configuração inicial.
4. Edite `plugins/MotdGuard/config.toml`.
5. Rode `/motdguard reload` para aplicar as mudanças sem reiniciar.

---

## ⚙️ Configuração

A configuração é simples, legível e direta — mude o comportamento do plugin sem recompilar nada.

**Arquivo principal:** `plugins/MotdGuard/config.toml`

```toml
[motd]
line1 = "<gradient:#f58220:#ffd9a8><bold>MeuServidor</bold></gradient>"
line2 = "<#ffffff>Protegido por <#f58220>MotdGuard"

[maintenance]
enabled = false
kick-message = "<red>Servidor em manutenção. Volte em breve!"

[rate-limit]
enabled = true
max-pings-per-minute = 60
block-message = "<gray>Muitas requisições. Aguarde."

[cooldown]
enabled = true
duration-seconds = 60

[messages]
reload-success = "<green>Configuração recarregada com sucesso."
reload-failure = "<red>Falha ao recarregar a configuração. Verifique o console."
maintenance-enabled = "<green>Modo manutenção ativado."
maintenance-disabled = "<green>Modo manutenção desativado."
maintenance-toggled = "<green>Modo manutenção {status}."
maintenance-status-enabled = "ativado"
maintenance-status-disabled = "desativado"
help-header = "<#f58220><bold>Comandos do MotdGuard:"
help-reload = "<yellow>/motdguard reload <gray>- Recarrega a configuração"
help-maintenance = "<yellow>/motdguard maintenance <gray>- Alterna o modo manutenção"
help-maintenance-on = "<yellow>/motdguard maintenance on <gray>- Ativa a manutenção"
help-maintenance-off = "<yellow>/motdguard maintenance off <gray>- Desativa a manutenção"
cooldown-message = "<red>Aguarde antes de usar outro comando."
```

> [!WARNING]
> As cinco seções — `[motd]`, `[maintenance]`, `[rate-limit]`, `[cooldown]` e `[messages]` — são **obrigatórias**. Uma seção ausente faz o plugin falhar ao carregar, com um erro claro `Missing [seção]`.

> [!NOTE]
> As mensagens usam a sintaxe **MiniMessage** (`<green>`, `<#f58220>`, `<bold>`). Códigos de cor legados `&` **não** são suportados e apareceriam literalmente.

---

## 🎨 MiniMessage

O MOTD e as mensagens usam [MiniMessage](https://docs.advntr.dev/minimessage/) para formatação moderna de texto — muito além do antigo padrão de cores simples.

| Sintaxe | Resultado |
| --- | --- |
| `<green>Online` | Cor nomeada |
| `<#f58220>MotdGuard` | Cor hexadecimal |
| `<bold>Texto</bold>` | Texto em negrito |
| `<gradient:#f58220:#ffffff>Servidor</gradient>` | Gradiente |
| `<hover:show_text:'Info'>Passe o mouse</hover>` | Texto com hover |

---

## ⌨️ Comandos

| Comando | Descrição | Permissão |
| --- | --- | --- |
| `/motdguard` · `/mg` | Mostra o menu de ajuda | `motdguard.admin` |
| `/motdguard reload` | Recarrega a configuração | `motdguard.admin` |
| `/motdguard maintenance` · `/mg m` | Alterna o modo manutenção | `motdguard.admin` |
| `/motdguard maintenance on` · `/mg m on` | Ativa o modo manutenção | `motdguard.admin` |
| `/motdguard maintenance off` · `/mg m off` | Desativa o modo manutenção | `motdguard.admin` |

---

## 🔑 Permissões

| Permissão | Descrição | Padrão |
| --- | --- | --- |
| `motdguard.admin` | Acesso aos comandos administrativos | `op` |
| `motdguard.bypass` | Permite entrar durante o modo manutenção | `false` |

---

## 🚀 Releases

As releases são publicadas a partir de tags de versão semântica.

```bash
git tag v1.0.0
git push origin v1.0.0
```

O workflow de release verifica se a tag corresponde à versão do projeto no Gradle, compila o plugin com Java 21, cria uma GitHub Release, gera as notas de versão e anexa o JAR compilado.

📥 Artefatos: [github.com/HanielCota/MotdGuard/releases](https://github.com/HanielCota/MotdGuard/releases)

---

## 🔨 Build Local

Use o Gradle Wrapper incluído no repositório.

```bash
# Build completo (testes, checagem de formatação, SpotBugs)
./gradlew build

# Build sem o SpotBugs
./gradlew build -x spotbugsMain -x spotbugsTest

# Gerar o JAR final (shaded)
./gradlew shadowJar
```

O artefato compilado é gerado em:

```text
build/libs/motdguard-1.0.0.jar
```

---

## 📁 Estrutura do Projeto

```text
src/main/java/io/github/hanielcota/motdguard/
├── MotdGuardPlugin.java
├── command/
│   └── MotdGuardCommand.java
├── config/
│   ├── ConfigData.java
│   ├── ConfigManager.java
│   ├── ConfigValidation.java
│   ├── CooldownConfig.java
│   ├── MaintenanceConfig.java
│   ├── MessagesConfig.java
│   ├── MotdConfig.java
│   └── RateLimitConfig.java
├── constants/
│   └── PluginConstants.java
├── listener/
│   ├── LoginListener.java
│   └── PingListener.java
├── maintenance/
│   └── MaintenanceManager.java
├── motd/
│   └── MotdProvider.java
├── ratelimit/
│   └── RateLimiter.java
└── util/
    ├── BucketFactory.java
    ├── CooldownService.java
    ├── IpExtractor.java
    ├── MiniMessageUtil.java
    └── PluginExceptionHandler.java
```

---

## 🔒 Qualidade & Segurança

O MotdGuard foi pensado para ser confiável em produção: proteger o proxy, manter a apresentação profissional e evitar que configuração ruim ou dependência velha vire dor de cabeça.

| Área | Ferramenta |
| --- | --- |
| 🏗️ Build | GitHub Actions |
| 🔍 Análise estática | CodeQL · SpotBugs · FindSecBugs |
| 🎯 Formatação | Spotless + Google Java Format |
| 📦 Dependências | Dependabot |
| 🧪 Testes | JUnit 5 · Mockito |
| 📤 Empacotamento | Shadow JAR |

---

## 💭 Filosofia

Um bom plugin não precisa ser pesado, confuso ou cheio de recursos desnecessários. Ele precisa **resolver o problema, proteger o servidor e continuar funcionando sem exigir atenção.**

O MotdGuard existe para entregar uma camada de controle limpa, forte e confiável para quem quer um proxy Velocity profissional.

---

## 🤝 Contribuindo

Contribuições são bem-vindas:

1. Faça um fork do repositório.
2. Crie uma branch para a sua mudança.
3. Rode o build localmente (`./gradlew build`).
4. Abra um pull request descrevendo o que mudou.

---

## 📄 Licença

Distribuído sob a **Licença MIT**. Veja [LICENSE](LICENSE) para mais detalhes.

<div align="center">
<br>

**Feito com ☕ para servidores Velocity sérios.**

<sub>Se o MotdGuard ajudar o seu servidor, considere deixar uma ⭐ no repositório.</sub>

</div>
