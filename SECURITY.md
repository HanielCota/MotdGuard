# Security Policy

## Supported Versions

MotdGuard is maintained from the `main` branch and the latest published release.

| Version | Supported |
| --- | --- |
| Latest release | Yes |
| `main` branch | Yes |
| Older releases | Best effort |

Security fixes are prioritized for the latest release. Older versions may receive fixes when the issue is severe and the change can be applied safely.

## Reporting a Vulnerability

Please do not open a public issue for security vulnerabilities.

Use GitHub private vulnerability reporting instead:

[Report a vulnerability](https://github.com/HanielCota/MotdGuard/security/advisories/new)

If GitHub private reporting is unavailable, contact the maintainer at `hanielcota@hotmail.com`.

When reporting a vulnerability, include as much detail as possible:

- A clear description of the issue.
- Steps to reproduce it.
- The affected MotdGuard version or commit.
- Your Velocity and Java versions.
- Any logs, proof of concept, or configuration needed to understand the impact.

## Response Expectations

| Step | Target |
| --- | --- |
| Initial acknowledgement | Within 72 hours |
| Triage and impact review | Within 7 days |
| Fix or mitigation plan | Based on severity and complexity |
| Public disclosure | After a fix or mitigation is available |

If the report is valid, the issue will be handled privately until a fix, mitigation, or clear user guidance is ready.

## Scope

Security reports are welcome for:

- MotdGuard plugin behavior.
- Ping rate limiting and maintenance mode bypass behavior.
- Configuration parsing and reload behavior.
- Build, release, and dependency security issues.

Reports outside the MotdGuard codebase, such as general Velocity server hardening or unrelated Minecraft server vulnerabilities, may be closed as out of scope.

## Safe Harbor

Good-faith security research is welcome. Please avoid destructive testing, public disclosure before coordination, accessing data that is not yours, or disrupting servers you do not own or have permission to test.
