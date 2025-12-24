# GitPM – Git Project Manager

GitPM is a desktop application designed to simplify and automate the management of your Git projects.

## Build & Run

Before building and installation make sure that your System fulfill the requirements:
- OS Windows 7+ or modern Unix System;
- at least 70 MB of free disk space;
- at least 1 GB of RAM;
- have Git installed.

There are three ways to run the Application, depending on your capabilities and goals.

### Source Code Building

This way is for developers who want to extend source code or build an up-to-date version of program.

You should already have installed:
- JDK (Java Development Kit) 21+ (you can download it from [official Oracle website](https://www.oracle.com/java/technologies/downloads/))
- Apache Maven 3.9.9+ (you can doanload it from [official Maven site](https://maven.apache.org/download.cgi))

Instruction:
1. Download source code of Project to your device: 
    ```bash
    git clone https://github.com/Honsage/GitPM.git
    ```
2. Build the Project: from root directory of Project (`GitPM/`) call:
    ```bash
   mvn clean package
    ```
3. Find built JAR: after building it would appear in `target/` directory.
4. Run the Application: run JAR using:
    ```bash
   java -jar target/"jar-filename".jar
    ```

### JAR Running

This method is recommended due to its cross-platform nature.

You should already have installed:
- JRE (Java Runtime Environment) 21+ (or JDK 21+);

Make sure that `java` command is available from command line. If it is not so then you should set up `JAVA_HOME` environment variable. 

Instruction:
1. Download JAR of Project: go to [Releases tab](https://github.com/Honsage/GitPM/releases) and download file with `.jar` extension.
2. Run the Application with:
    ```bash
   java -jar "jar-filename".jar
    ```

### Installation

This is the simplest way for users with Windows OS.

Instruction:
1. Download installer: go to [Releases tab](https://github.com/Honsage/GitPM/releases) and download file with `.exe` extension.
2. Install the Application: run the installer and follow Installation Master's instructions.
3. Run the Application: open application via shortcut on desktop or 