# MiniBank — Java Banking Management System

**Object-Oriented Programming (CEUE203) · Part B Project · Practicals 1–14**

[![Java](https://img.shields.io/badge/Java-21%20LTS-orange.svg)](https://www.oracle.com/java/)
[![Build](https://img.shields.io/badge/Build-javac%20%7C%20jar-green.svg)](#build-and-run)
[![Scope](https://img.shields.io/badge/Scope-Part%20B%20%7C%20Practicals%201--14-purple.svg)](#practical-wise-implementation)

MiniBank is a modular, console-based banking application developed as the **Part B project** for the CEUE203 Object-Oriented Programming laboratory. It brings the practicals together into one Java project, progressing from object-oriented fundamentals to validation, exception handling, concurrency, persistence, collections, generics, layered services, and testing.

> **Scope:** This README documents the Part B MiniBank project only. Standalone Part A exercises should remain separate unless the official practical file explicitly says otherwise.

## Contents

- [Project Overview](#project-overview)
- [Objectives](#objectives)
- [Features](#features)
- [Practical-wise Implementation](#practical-wise-implementation)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Build and Run](#build-and-run)
- [Interactive Usage](#interactive-usage)
- [Sample Output](#sample-output)
- [Testing and Verification](#testing-and-verification)
- [Java Concepts Demonstrated](#java-concepts-demonstrated)
- [Known Limitations](#known-limitations)
- [Viva Preparation](#viva-preparation)
- [Academic Context](#academic-context)

## Project Overview

MiniBank models common banking operations through a command-line interface and practical-wise demonstrations. The design separates domain models, business services, utility functions, custom exceptions, annotations, and tests into packages.

The project is intended to demonstrate Java language concepts through a single coherent application. It is an educational project, not a production banking system; it does not provide production-grade security, real financial integrations, or regulatory controls.

## Objectives

- Apply encapsulation, constructors, access modifiers, and static members.
- Model banking accounts using inheritance, abstraction, and runtime polymorphism.
- Validate customer data and transaction commands.
- Use interfaces, functional interfaces, lambdas, annotations, and reflection.
- Handle failures with meaningful exceptions and safe resource management.
- Demonstrate thread coordination, synchronization, executor services, and safe transfers.
- Persist application state and write transaction logs where implemented.
- Use Java collections and generics to organize account data.
- Connect the components through a service layer and verify key behavior with tests.

## Features

### Account hierarchy

- **`Account`**: Abstract base class for common account state and transaction behavior.
- **`SavingsAccount`**: Savings-specific balance rules and 4% annual interest as described in the project implementation.
- **`CurrentAccount`**: Current account behavior with an overdraft limit and 0% interest.
- **`FixedDepositAccount`**: Fixed-deposit behavior with 7% interest and restricted withdrawals according to its rules.
- Account numbers follow the `AC0001` style; customer identifiers follow the `CUST101` style in the documented demonstrations.

Interest rates and account rules are educational examples and should be adjusted to match the exact requirements in the college practical file.

### Customer, validation, and commands

- Customer profiles and a nested `Customer.Address` model.
- Validation utilities for mobile numbers, email, PAN, IFSC, and transaction amounts.
- A `Command` record and `CommandParser` for structured transaction commands.
- `StatementFormatter` for readable account summaries.

### Interfaces and functional programming

- `Transactable` for deposit and withdrawal contracts.
- `InterestBearing` for default interest calculation methods.
- `WithdrawRule` for configurable withdrawal checks using lambdas or anonymous classes.
- `Premium` marker interface for runtime capability checks.

### Annotations, exceptions, and resource handling

- Runtime annotations such as `@Id`, `@Positive`, and `@MaxLength`.
- Reflection-based validation using `AnnotationValidator`.
- A checked exception hierarchy including `BankException`, `InsufficientFundsException`, `InvalidAmountException`, and `AccountNotFoundException`.
- `BankSession` demonstrates `AutoCloseable` and try-with-resources.
- Additional annotations or exceptions, such as `@Audit` and `DailyLimitExceededException`, should be documented only where they exist in the final source.

### Concurrency and transaction processing

- `AccountWorker` demonstrates concurrent account operations.
- Synchronized account methods protect shared state where required.
- `TransactionProcessor` uses an `ExecutorService` thread pool.
- `TransactionBuffer` demonstrates producer-consumer coordination with `wait()` and `notify()`/`notifyAll()`.
- Safe transfers can acquire account locks in a consistent order to avoid circular-wait deadlocks.

### Persistence, collections, and generics

The later practicals extend the project with these components, as described by the current project documentation:

- `StatePersister` for object serialization and deserialization.
- `TransactionLog` for append-only transaction logging.
- `ReportGenerator` for Java NIO directory/log reporting.
- `AccountStore` for account lookup, storage, and sorting using collections.
- A generic `Repository<T>` using reflection to locate an `@Id` field.
- `BankService` to coordinate application workflows and connect the interactive menu to the supporting components.

## Practical-wise Implementation

The following matrix consolidates the original README and the newer Part B documentation. **Confirm the titles and statuses against the official practical file and the current source code before submission.**

| Practical | Main concepts | Key project components |
|---|---|---|
| **1** | Project setup, `BankInfo` record, `MenuOption` enum, console menu, `Scanner`, switch expression | `BankInfo`, `MenuOption`, `MiniBank` |
| **2** | Encapsulation, constructors, access modifiers, static identifiers, deposits and withdrawals | `Customer`, `Account` |
| **3** | `toString()`, `equals()`, `hashCode()`, cloning, nested classes | `Account`, `Customer.Address`, `Customer.clone()` |
| **4** | Regular expressions, strings, records, tokenization, formatting | `Validator`, `Command`, `CommandParser`, `StatementFormatter` |
| **5** | Abstract classes, inheritance, polymorphism, `super()` | `Account`, `SavingsAccount`, `CurrentAccount`, `FixedDepositAccount` |
| **6** | Interfaces, default methods, lambdas, anonymous classes, marker interface, packages and JAR | `Transactable`, `InterestBearing`, `WithdrawRule`, `Premium` |
| **7** | Custom annotations, reflection, metadata validation | `@Id`, `@Positive`, `@MaxLength`, `AnnotationValidator` |
| **8** | Checked exceptions, exception hierarchy, try-with-resources | `BankException` and subclasses, `BankSession` |
| **9** | `Runnable`, thread lifecycle, `join()`, race conditions, synchronization | `AccountWorker`, synchronized account operations |
| **10** | `ExecutorService`, producer-consumer coordination, deadlock avoidance | `TransactionProcessor`, `TransactionBuffer`, safe transfer logic |
| **11** | Serialization, persistence, Java NIO transaction logging and reports | `StatePersister`, `TransactionLog`, `ReportGenerator` |
| **12** | Collections, maps, lists, `Comparable`, `Comparator`, thread-safe maps | `AccountStore`, account ordering, shared account storage |
| **13** | Generics, bounded types, generic repository, reflection-based identifiers | `Repository<T>`, `@Id` resolution, bounded generic methods |
| **14** | Layered architecture, application integration, audit hooks, unit testing | `BankService`, `MiniBank`, `AuditLogger`, test runner and tests |

A practical should be marked **Complete** only when its required implementation is present and the corresponding behavior has actually been verified. A README entry by itself is not proof of completion.

## Technology Stack

| Area | Technology |
|---|---|
| Language | Java; project documentation targets JDK 21 |
| Build | Standard JDK tools: `javac`, `java`, and `jar` |
| Paradigms | Object-oriented, functional, declarative, and concurrent programming |
| I/O | Java standard I/O and, where implemented, Java NIO |
| Concurrency | `Thread`, `Runnable`, synchronization, `ExecutorService`, and concurrent collections |
| Testing | Project's test runner and assertions, if included in the checked-out source |
| External dependencies | No external dependencies are required by the documented core command-line build; verify against the final source tree |

## Architecture

MiniBank follows a package-based separation of responsibilities. If the actual source tree differs, update this diagram to match it.

```text
MiniBank
├── Presentation / Entry Points
│   ├── service.MiniBank
│   └── service.Main
├── Service Layer
│   ├── BankService
│   ├── TransactionProcessor
│   ├── TransactionBuffer
│   ├── AccountStore
│   └── Repository<T>
├── Domain Model
│   ├── Account
│   ├── SavingsAccount
│   ├── CurrentAccount
│   ├── FixedDepositAccount
│   ├── Customer
│   └── Interfaces, records, and enums
├── Utilities
│   ├── Validator
│   ├── CommandParser
│   ├── StatementFormatter
│   ├── AnnotationValidator
│   ├── StatePersister
│   ├── TransactionLog
│   └── ReportGenerator
├── Custom Exceptions
├── Runtime Annotations
└── Tests
```

## Project Structure

The following is a reference structure based on the two README versions. Keep only files that actually exist in the final project; do not create empty files just to match this list.

```text
MINI BANK/
├── README.md
├── .gitignore
├── Manifest.txt
├── minibank.jar                 # generated build artifact, if retained
├── model/
│   ├── Account.java
│   ├── BankInfo.java
│   ├── Command.java
│   ├── Customer.java
│   ├── SavingsAccount.java
│   ├── CurrentAccount.java
│   ├── FixedDepositAccount.java
│   ├── Transactable.java
│   ├── InterestBearing.java
│   ├── WithdrawRule.java
│   ├── Premium.java
│   ├── MenuOption.java
│   ├── TransactionType.java
│   └── annotation/
├── exception/
├── util/
├── service/
└── test/                         # if present
```

Runtime folders such as `data/` and `logs/` may be generated by the application. Avoid committing generated `.class` files or build output unless your instructor specifically requires them.

## Prerequisites

- JDK 21 recommended; use the Java version required by the practical file.
- PowerShell or another terminal.
- Ensure `java`, `javac`, and (for JAR packaging) `jar` are available in `PATH`.

Check your installation:

```powershell
java -version
javac -version
jar --version
```

## Build and Run

Run these commands from the **project root**. They assume the package directories shown below exist. If your final project contains additional source folders or has a different entry point, adjust the command accordingly.

### 1. Clean the previous build

```powershell
Remove-Item -Recurse -Force out -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force out
```

### 2. Compile the packaged project

```powershell
javac -d out model\*.java model\annotation\*.java exception\*.java util\*.java service\*.java
```

If the project includes the custom test framework in `test/`, compile it too:

```powershell
javac -d out model\*.java model\annotation\*.java exception\*.java util\*.java service\*.java test\*.java test\annotation\*.java
```

Use the command that matches the directories and files actually present. If compilation reports a missing directory or symbol, correct the command or source before proceeding; do not treat a failed build as successful.

### 3. Run the practical demonstration suite

```powershell
java -cp out service.Main
```

Use this only if `service.Main` exists and is the integrated demonstration entry point in your current project.

### 4. Run the interactive MiniBank application

```powershell
java -cp out service.MiniBank
```

### 5. Run the test suite, if included

```powershell
java -cp out test.TestRunner
```

Run this command only if `test.TestRunner` exists in the project.

### 6. Package and run the JAR, if required

Ensure `Manifest.txt` contains a valid `Main-Class` entry matching the actual interactive entry point, for example:

```text
Main-Class: service.MiniBank
```

Then package the compiled classes:

```powershell
jar cfm minibank.jar Manifest.txt -C out .
java -jar minibank.jar
```

If `jar` is not on `PATH`, install/configure the JDK correctly or use the full path to the JDK's `jar.exe`. Do not rely on a machine-specific path in the README.

## Interactive Usage

The documented interactive application is intended to provide menu-driven banking operations such as:

1. Open an account.
2. Deposit funds.
3. Withdraw funds.
4. Transfer funds.
5. View an account statement.
6. List accounts.
7. Process batch transactions, if implemented.
8. Generate a report, if implemented.
9. Exit and persist state, if implemented.

The exact menu options depend on the current `MiniBank.java`; keep this list synchronized with the real menu.

### Command parsing

The command parser described in the project supports a transaction format such as:

```text
DEPOSIT AC0001 500
WITHDRAW AC0002 1500
```

Use only commands supported by the actual parser. A transfer command may require both source and destination account numbers; confirm its exact syntax in `CommandParser.java` before documenting it.

## Sample Output

The original project demonstration included output similar to the following for the first ten practicals. Treat this as an illustrative example, not a guarantee of the current build's exact output; regenerate it from the final code before submission.

```text
MINIBANK PRACTICAL SUITE

[PRACTICAL 1–5] Basic Models, Accounts & Validation
Bank Header : BankInfo[name=MiniBank, branch=Dhrangadhra]
Validator Mobile ('9876543210'): true
Validator Email ('riya@gmail.com'): true

[PRACTICAL 6] Interfaces, Default Methods & Lambdas
WithdrawRule (Anonymous): true
WithdrawRule (Lambda): true

[PRACTICAL 7] Annotations & Reflection Validator
Validation on valid account: 0 errors

[PRACTICAL 8] Exception Hierarchy & Try-With-Resources
Caught InsufficientFundsException

[PRACTICAL 9] Multithreading & Synchronization
Final balance after 10 threads × 1,000 deposits: 10000

[PRACTICAL 10] Thread Pool, Producer-Consumer & Deadlock Avoidance
Pool tasks completed
Safe transfers completed
```

For Practical 11–14, add real output from the final implementation showing persistence, collections, generics, integration, and test execution as applicable. Never paste an output that was not generated by your code and environment.

## Testing and Verification

Before submitting, perform a clean compilation and run each available demonstration. Verify actual behavior rather than relying only on printed success messages.

Recommended checks:

- Valid and invalid customer/account inputs.
- Valid deposits and withdrawals.
- Rejection of non-positive transaction amounts.
- Insufficient-balance handling without corrupting balances.
- Transfers preserving the total balance, including failed-transfer cases.
- Account persistence across application restarts, if implemented.
- Transaction logs and report generation, if implemented.
- Collection lookups and sorting.
- Generic repository behavior for supported entity types.
- Concurrent deposits producing the expected final balance.
- Thread pool shutdown and producer-consumer completion.
- Unit tests passing with the final source tree.

Record the actual commands and results below after running them:

| Check | Command | Result |
|---|---|---|
| Clean compilation | `javac ...` | Update after execution |
| Practical demonstrations | `java -cp out service.Main` | Update after execution |
| Unit tests | `java -cp out test.TestRunner` | Update after execution, if available |
| Runnable JAR | `java -jar minibank.jar` | Update after execution, if packaged |

Do not claim “100% passing”, “zero defects”, or “fully verified” unless the tests were actually executed and those results were observed.

## Java Concepts Demonstrated

- **Encapsulation:** controlled access to account state.
- **Inheritance and polymorphism:** specialized account types share the `Account` abstraction.
- **Records and enums:** structured immutable data and fixed sets of options.
- **Collections and generics:** reusable storage and type-safe operations.
- **Functional interfaces and lambdas:** behavior passed as a parameter.
- **Annotations and reflection:** runtime metadata and validation.
- **Exception handling:** domain-specific failure reporting.
- **Concurrency:** threads, synchronization, executor services, and inter-thread coordination.
- **Serialization and NIO:** persistence and file/report operations, where implemented.
- **Layered design and testing:** separation of application responsibilities and verification of behavior.

## Known Limitations

- This is an educational console application, not a production banking system.
- Fixed-deposit withdrawal rules may prohibit early withdrawal; document the exact behavior from the implementation.
- Serialization-based persistence is not a substitute for a secure transactional database.
- Console behavior and menu options depend on the current source code.
- Features not present in the repository must be removed from this README or implemented and tested before being described as complete.

## Viva Preparation

**What is runtime polymorphism?**  
A call to an overridden method is resolved according to the actual object's type at runtime. MiniBank can use an `Account` reference to invoke the account-type-specific interest or withdrawal behavior.

**How can lock ordering prevent deadlocks?**  
If every transfer acquires the two account locks in the same global order, opposite-direction transfers cannot create a circular wait due solely to acquiring those locks in opposite orders.

**Why use a generic repository?**  
A generic repository can provide reusable storage operations for multiple entity types while preserving compile-time type safety. Reflection-based ID lookup is possible where the implementation explicitly supports it.

**Why use `serialVersionUID`?**  
It identifies the serialized class version used during deserialization compatibility checks. Classes intended for long-term serialized storage should define and manage it deliberately.

## Academic Context

- **Course:** Object Oriented Programming (CEUE203)
- **Project:** MiniBank — Part B (Practicals 1–14)
- **Academic year:** 2026–2027
- **Repository:** [harshkunpara/MiniBank](https://github.com/harshkunpara/MiniBank)
- **Submitted by:** Harsh Kunpara

---

This project is intended for educational and academic evaluation purposes. Confirm the official practical requirements, actual source tree, and test results before final submission.
