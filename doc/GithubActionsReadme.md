# GitHub Actions - Automatisierte Workflows für dein Projekt

## Was ist GitHub Actions?
GitHub Actions ist eine CI/CD-Plattform (Continuous Integration / Continuous Deployment), die es ermöglicht, automatisierte Workflows direkt in GitHub zu erstellen. Damit können Builds, Tests und Deployments automatisiert werden.

## Vorteile von GitHub Actions
- **Automatisierung**: wiederkehrende Aufgaben wie Tests, Builds und Deployments werden automatisiert.
- **Integrierte GitHub-Unterstützung**: Läuft direkt auf GitHub ohne zusätzliche Tools.
- **Flexibilität**: Workflows lassen sich individuell anpassen.
- **Skalierbarkeit**: Unterstützt verschiedene Umgebungen und Betriebssysteme.

## Erste Schritte
### 1. Erstellen eines Workflows
Workflows werden in YAML-Dateien unter `.github/workflows/` gespeichert.

### 2. Beispiel für eine einfache GitHub Actions Workflow-Datei
Erstelle die Datei `.github/workflows/main.yml` mit folgendem Inhalt:

```yaml
name: Java Code Check

on: [push, pull_request]

jobs:
  code-check:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v3
        with:
          distribution: 'oracle'
          java-version: '21'
          cache: 'maven'
      - run: mvn verify
```


## Erklärung des Workflows
- **`on`**: Der Workflow wird bei einem `push` oder `pull_request` ausgelöst.
- **`jobs`**: Definiert Aufgaben, die ausgeführt werden.
- **`runs-on`**: Legt fest, auf welchem Betriebssystem der Workflow läuft (`ubuntu-latest`).
- **`steps`**: Die einzelnen Schritte des Workflows:
  1. Klont den Code
  2. Installiert Oracle JDK 21
  3. Führt mvn verify aus, um den Code zu prüfen

## Fazit
GitHub Actions ist ein mächtiges Werkzeug zur Automatisierung von Entwicklungsprozessen. Es ermöglicht eine nahtlose CI/CD-Integration direkt in GitHub.

## Live-Demo
- Erstellen eines Workflows aus einer Vorlage
- Commit mit Auslösen des Continuous Integration Workflows mit Fehler (z.B. aus IntelliJ heraus)

## Diese Readme wurde mit Hilfe von KI erstellt
