# GitHub Actions - Automatisierte Workflows für dein Projekt

## Was ist GitHub Actions?
GitHub Actions ist eine CI/CD-Plattform (Continuous Integration / Continuous Deployment), die es ermöglicht, automatisierte Workflows direkt in GitHub zu erstellen. Damit können Builds, Tests und Deployments automatisiert werden.

## Vorteile von GitHub Actions
- **Automatisierung**: Prozesse wie Tests, Builds und Deployments werden automatisiert.
- **Integrierte GitHub-Unterstützung**: Läuft direkt auf GitHub ohne zusätzliche Tools.
- **Flexibilität**: Workflows lassen sich individuell anpassen.
- **Skalierbarkeit**: Unterstützt verschiedene Betriebssysteme und Umgebungen.

## Erste Schritte
### 1. Erstellen eines Workflows
Workflows werden in YAML-Dateien unter `.github/workflows/` gespeichert.

### 2. Beispiel für eine einfache GitHub Actions Workflow-Datei
Erstelle die Datei `.github/workflows/main.yml` mit folgendem Inhalt:

```yaml
name: CI Pipeline

on:
  push:
    branches:
      - main
  pull_request:
    branches:
      - main

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - name: Repository auschecken
        uses: actions/checkout@v4
      
      - name: Setze Node.js ein
        uses: actions/setup-node@v3
        with:
          node-version: '18'
      
      - name: Installiere Abhängigkeiten
        run: npm install
      
      - name: Führe Tests aus
        run: npm test
```

## Erklärung des Workflows
- **`on`**: Der Workflow wird bei einem `push` oder `pull_request` auf `main` ausgelöst.
- **`jobs`**: Definiert Aufgaben, die ausgeführt werden.
- **`runs-on`**: Legt fest, auf welchem Betriebssystem der Workflow läuft (`ubuntu-latest`).
- **`steps`**: Die einzelnen Schritte des Workflows:
  1. Checkt den Code aus dem Repository aus.
  2. Installiert Node.js.
  3. Installiert die Projektabhängigkeiten.
  4. Führt Tests aus.

## Weitere Möglichkeiten
- **Artefakte speichern**: Build-Ergebnisse können mit `actions/upload-artifact@v3` gespeichert werden.
- **Deployment**: Workflows können für das Deployment in die Cloud genutzt werden.
- **Benutzerdefinierte Aktionen**: Eigene GitHub Actions lassen sich erstellen und verwenden.

## Fazit
GitHub Actions ist ein mächtiges Werkzeug zur Automatisierung von Entwicklungsprozessen. Es ermöglicht eine nahtlose CI/CD-Integration direkt in GitHub.
