# Log2Graylog

[![Java Version](https://img.shields.io/badge/Java-17-blue.svg)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

A command-line tool that parses log messages from files and forwards them to a Graylog server using the GELF HTTP input protocol.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Installation](#installation)
  - [Prerequisites](#prerequisites)
  - [Building from Source](#building-from-source)
- [Usage](#usage)
  - [Command Line Options](#command-line-options)
  - [Examples](#examples)
- [Architecture](#architecture)
- [Development](#development)
  - [Project Structure](#project-structure)
  - [Testing](#testing)
- [Possible Improvements](#possible-improvements)
- [Author](#author)
- [License](#license)

## Overview

Log2Graylog is a command-line utility for parsing log files in JSON or CSV format (other formats may be supported in future) and sending the content to a Graylog server.
The app converts the log messages into the Graylog Extended Log Format (GELF) format and pushes them to the GELF HTTP input endpoint of the Graylog server.

![Graylog Screenshot showing log messages processed by Log2Graylog](docs/images/graylog-screenshot.png)  
*Screenshot of Graylog after importing log messages using Log2Graylog*

## Features

- Parse log messages from files with specific formats (JSON, CSV)
- Extract and map fields from log messages to GELF format
- Add additional metadata to log entries
- Send GELF messages to a Graylog server over HTTP
- Options for specifying the Graylog server URL and timeout

## Installation

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- A running Graylog server with a GELF HTTP input configured (see https://github.com/Graylog2/graylog-project)

### Building from Source

1. Clone the repository:
   ```bash
   git clone https://github.com/rolandort/log2graylog.git
   cd log2graylog
   ```

2. Build the application using Maven:
   ```bash
   mvn clean package
   ```

This will create an executable JAR file in the `target` directory.

## Usage

### Command Line Options

```
Usage: Log2Graylog [-hvV] [-p=<parserType>] [-s=<senderType>] [-t=<timeout>]
                   [-u=<graylogUrl>] LOG_FILE
Parses log messages and send them to Graylog using the GELF format.
      LOG_FILE              Logfile to parse as input
  -h, --help                Show this help message and exit.
  -p, --parser=<parserType> Parser type (JSON|CSV, default: JSON)
  -s, --sender=<senderType> Sender type (SIMULATE|HTTP, default: HTTP)
  -t, --timeout=<timeout>   Timeout of HTTP requests in seconds. (default: 10
                              sec)
  -u, --url=<graylogUrl>    Output URL of the Graylog GELF HTTP interface
                              (default: http://localhost:12202/gelf)
  -v, --verbose             Enable verbose output
  -V, --version             Print version information and exit.
```

### Examples

Basic usage with JSON log file, HTTP sender and default Graylog URL (http://localhost:12202/gelf):
```bash
java -jar target/log2graylog-1.1-SNAPSHOT.jar sample-messages.txt
```

Custom Graylog URL and timeout
```bash
java -jar target/log2graylog-1.1-SNAPSHOT.jar -v --timeout 30 --url http://graylog-server:12202/gelf sample-messages.txt
```

CSV log file and simulated sender
```bash
java -jar target/log2graylog-1.1-SNAPSHOT.jar --parser CSV --sender SIMULATE sample-messages.csv
```

Example source JSON log message:

```json
{
  "ClientDeviceType": "desktop",
  "ClientIP": "192.168.211.15",
  "ClientIPClass": "noRecord",
  "ClientStatus": 550, 
  "ClientRequestBytes": 63,
  "ClientRequestReferer": "torch.sh",
  "ClientRequestURI": "/search",
  "ClientRequestUserAgent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64)",
  "ClientSrcPort":450,
  "EdgeServerIP": "10.0.88.33",
  "EdgeStartTimestamp": 1576929197,
  "DestinationIP": "172.16.45.194",
  "OriginResponseBytes": 957,
  "OriginResponseTime": 398000000
}
```
Example source CSV log message:

```csv
ClientDeviceType,ClientIP,ClientIPClass,ClientStatus,ClientRequestBytes,ClientRequestReferer,ClientRequestURI,ClientRequestUserAgent,ClientSrcPort,EdgeServerIP,EdgeStartTimestamp,DestinationIP,OriginResponseBytes,OriginResponseTime
desktop,192.168.237.181,noRecord,200,927,torch.sh,/search,"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_5) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/12.1.1 Safari/605.1.15",564,10.0.56.97,1576929197,172.16.101.17,571,296000000
```

## Architecture

Log2Graylog uses a modular architecture with the following components:

1. **CLI** - Command-line interface library for Java
2. **Log Parser** - Reads and parses log files into structured data
3. **GELF Formatter** - Converts parsed log entries to GELF format
4. **HTTP Sender** - Sends GELF messages to the Graylog server

### Dependencies

- PicoCli - Command-line interface library for Java
- Google Guice - Dependency injection framework
- Gson - JSON parsing library
- Opencsv - CSV parsing library
- Log4j - Logging framework
- Lombok - Code generation library
- Junit - Unit testing framework

## Development

### Project Structure

```
src/main/java/org/rolandort/
├── Main.java                  # Application entry point
├── cli/                       # Command-line interface
├── di/                        # Dependency injection
├── formatter/                 # GELF message formatting
├── model/                     # Data models
├── parser/                    # Log file parsing
├── sender/                    # HTTP communication
└── service/                   # Business logic
```

### Testing

To run the tests:
```bash
mvn test
```

## Possible Improvements

- **Feature Additions**:
  - [x] Support for additional log formats with option to select the format
  - [ ] Configurable log format mapping in a JSON file
  - [ ] Accept multiple logfiles as input
  - [x] Dry-run option - DONE (using --sender SIMULATE)

- **Performance Enhancements**:
  - [ ] Support for large log files (10+ GB) through chunking and parallel processing
  - [ ] Performance optimizations using asynchronous HTTP requests (using CompletableFuture or PushPromiseHandler)

- **Technical Debt**:
  - [x] Improve naming conventions for formatters based on log sources
  - [ ] Enhance error handling and reporting
  - [ ] Additional unit tests
  - [ ] provide stats on import e.g. throughput, error rate, max/min timestamp, ... 
  - [ ] Log file rotation config in log4j2.xml
  - [ ] Direct integration with Log4J2 GELF appender

## Author

This project was created by [Roland Ortner](https://www.linkedin.com/in/roland-ortner/).

## License

This project is licensed under the MIT License - see the LICENSE file for details.
