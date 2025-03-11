# Maven - Build Automation Tool

## Was ist Maven?
Apache Maven ist ein leistungsfähiges Build-Automatisierungswerkzeug, das hauptsächlich für Java-Projekte verwendet wird. Es basiert auf einem deklarativen Ansatz und nutzt XML zur Konfiguration. Maven erleichtert die Verwaltung von Abhängigkeiten, den Build-Prozess und das Deployment von Projekten.

## Installation

### Voraussetzungen
- Java Development Kit (JDK) 8 oder höher
- Ein funktionierendes Internet für den Download von Abhängigkeiten

### Installation unter Windows/Linux/macOS
1. Lade die neueste Version von Maven von der [offiziellen Apache Maven-Website](https://maven.apache.org/download.cgi) herunter.
2. Entpacke das Archiv an einen gewünschten Speicherort.
3. Füge den `bin`-Pfad von Maven zu deiner `PATH`-Umgebungsvariable hinzu.
4. Überprüfe die Installation mit:
   ```sh
   mvn -version
   ```

## Projekt erstellen
Ein neues Maven-Projekt kann mit folgendem Befehl erstellt werden:
```sh
mvn archetype:generate -DgroupId=com.beispiel -DartifactId=meinprojekt -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false
```

## Projektstruktur
Ein typisches Maven-Projekt hat die folgende Struktur:
```
meinprojekt/
│── src/
│   ├── main/java/ (Quellcode)
│   ├── test/java/ (Tests)
│── pom.xml (Projektkonfigurationsdatei)
```

## Wichtige Maven-Befehle
- **Projekt kompilieren**: `mvn compile`
- **Tests ausführen**: `mvn test`
- **JAR erstellen**: `mvn package`
- **Abhängigkeiten verwalten**: `mvn dependency:tree`
- **Projekt bereinigen**: `mvn clean`

## `pom.xml` - Die zentrale Konfigurationsdatei
Die `pom.xml` Datei enthält Metadaten über das Projekt sowie die Abhängigkeiten. Beispiel:
```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.beispiel</groupId>
    <artifactId>meinprojekt</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <dependencies>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.13.2</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```

## Fazit
Maven vereinfacht die Verwaltung von Java-Projekten erheblich, indem es Abhängigkeiten handhabt, den Build-Prozess steuert und Standardstrukturen bereitstellt. Es ist ein unverzichtbares Werkzeug für moderne Java-Entwicklung.

## Live-Demo
- Erstellen eines Projekts mit simplen Maven-Projekts (Archtype)
- Arbeiten mit Maven, Github und IntelliJ

## Diese Readme wurde mit Hilfe von KI erstellt
