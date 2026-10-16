# MiniBank — Java Banking Management System

[![Java Version](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Architecture](https://img.shields.io/badge/Architecture-Modular%20Package--Based-blue.svg)](#project-architecture)
[![Build Tool](https://img.shields.io/badge/Build-javac%20%7C%20jar-green.svg)](#compilation-and-execution)
[![Course](https://img.shields.io/badge/Course-CEUE203%20OOP-purple.svg)](#academic-context-and-credits)
[![Status](https://img.shields.io/badge/Practicals%201--10-Completed%20%26%20Verified-brightgreen.svg)](#practical-wise-implementation)

MiniBank is a modular, console-based banking management system developed in Java. Built incrementally across university laboratory sessions for **Object Oriented Programming (CEUE203)**, the project transitions from core object-oriented fundamentals to enterprise-style Java paradigms—including polymorphic domain hierarchies, functional interfaces, reflection-based annotation validation, custom exception hierarchies, multithreading synchronization, managed thread pools, and deadlock-free transaction processing.

---

## Table of Contents

- [Project Overview](#project-overview)
- [Project Goals](#project-goals)
- [Features](#features)
- [Practical-Wise Implementation](#practical-wise-implementation)
- [Technology Stack](#technology-stack)
- [Project Architecture](#project-architecture)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Installation and Setup](#installation-and-setup)
- [Compilation and Execution](#compilation-and-execution)
- [Usage Guide](#usage-guide)
- [Sample Output](#sample-output)
- [Java Concepts Demonstrated](#java-concepts-demonstrated)
- [Testing and Verification](#testing-and-verification)
- [Learning Outcomes](#learning-outcomes)
- [Current Limitations](#current-limitations)
- [Future Improvements](#future-improvements)
- [Academic Context and Credits](#academic-context-and-credits)
- [License](#license)

---

## Project Overview

MiniBank simulates core retail banking workflows through a clean, menu-driven command-line interface and an automated test suite. Unlike trivial starter projects, MiniBank is structured as a real-world software system that emphasizes clean code separation, defensive validation, type safety, and robust concurrent execution.

The codebase is organized into dedicated packages (`model`, `model.annotation`, `exception`, `util`, and `service`), ensuring a clear separation of concerns between domain entities, validation utilities, error-handling mechanisms, and transaction execution engines.

---

## Project Goals

- **Encapsulation & Domain Integrity**: Ensure bank account balances can never be modified directly or set to inconsistent states.
- **Polymorphism in Banking Rules**: Support specialized account types (Savings, Current, Fixed Deposit) with distinct interest calculation and withdrawal rules via dynamic method dispatch.
- **Defensive Boundary Validation**: Intercept malformed customer data (PAN, IFSC, mobile, email) and invalid transactions before processing.
- **Declarative Metadata Validation**: Use custom annotations and reflection to inspect and validate domain entity constraints at runtime.
- **Fail-Safe Exception Handling**: Replace ambiguous return flags with an informative, domain-specific checked exception hierarchy and reliable resource management.
- **Thread Safety & Race Condition Prevention**: Protect shared account state in multi-user concurrent environments using Java synchronization.
- **Asynchronous Execution & Coordination**: Execute transaction batches using a managed thread pool, coordinate producer-consumer queues using `wait()`/`notify()`, and prevent deadlocks using strict lock ordering.

---

## Features

### 1. Account Management & Hierarchy
- **Abstract Base Account**: Encapsulates unique account number generation (`AC0001`, `AC0002`), account holder name, balance in whole rupees, active status, and abstract rules.
- **Savings Account**: Implements a minimum balance constraint (`minBalance`) and yields 4.0% annual interest. Marked with the `Premium` marker interface.
- **Current Account**: Supports an overdraft facility (`overdraftLimit`) allowing balances to fall below zero up to the authorized limit; yields 0.0% interest.
- **Fixed Deposit Account**: Locked deposit model with a fixed 7.0% interest rate where withdrawals are disallowed prior to maturity.

### 2. Customer & Address Modeling
- **Customer Profiles**: Auto-generates unique customer identifiers (`CUST101`, `CUST102`), storing contact details and linked addresses.
- **Static Nested Address**: Encapsulates street line, city, state, and postal code.
- **Object Cloning**: Implements `Cloneable` and overrides `clone()` to support safe customer profile duplication.

### 3. Validation & Parsing Engine
- **Regex Validation**: High-performance pre-compiled `Pattern` matching for Indian mobile numbers (`^[6-9][0-9]{9}$`), email addresses, PAN cards (`^[A-Z]{5}[0-9]{4}[A-Z]$`), and IFSC codes (`^[A-Z]{4}0[A-Z0-9]{6}$`).
- **Command Parser**: Tokenizes input strings like `DEPOSIT AC0001 500` into immutable `Command` records with strict argument verification.
- **Statement Formatter**: Generates multi-line formatted account statements utilizing `StringBuilder`.

### 4. Interface Contracts & Functional Programming
- **Transactable**: Establishes standard banking transaction contracts (`deposit` and `withdraw`).
- **InterestBearing**: Features default methods `yearlyInterest()` and `monthlyInterest()`, calculating interest dynamically from polymorphic subtype rates.
- **WithdrawRule**: Functional interface (`@FunctionalInterface`) supporting customizable withdrawal policies using anonymous inner classes and lambda expressions.
- **Premium**: Marker interface used for runtime capability checks (`instanceof Premium`).

### 5. Custom Annotations & Reflection Framework
- **Custom Metadata**:
  - `@Id`: Field-level runtime marker for primary keys.
  - `@Positive`: Field-level constraint validating that numeric values exceed zero, with customizable error messages.
  - `@MaxLength`: Field-level constraint enforcing maximum string lengths.
- **AnnotationValidator**: Reflection utility that inspects object fields via `getDeclaredFields()`, checks annotation presence, breaks private encapsulation via `setAccessible(true)`, and returns human-readable validation error messages.

### 6. Robust Error Handling
- **Checked Exception Hierarchy**: `BankException` base class with specialized subclasses:
  - `InsufficientFundsException`: Carries the exact monetary `shortfall`.
  - `InvalidAmountException`: Rejects non-positive transaction amounts.
  - `AccountNotFoundException`: Guards transfer operations against missing destination targets.
- **Try-With-Resources**: Demonstrates `AutoCloseable` lifecycle management using a `BankSession` resource that guarantees clean closure.

### 7. Multithreading & Synchronization
- **AccountWorker**: `Runnable` worker executing multiple concurrent deposits against a single account.
- **Thread Lifecycle Monitoring**: Logs thread transitions (`NEW`, `RUNNABLE`, `TERMINATED`) using explicit `Thread.join()` coordination.
- **Race Condition Prevention**: Synchronized deposit and withdrawal methods ensure deterministic account balances under heavy concurrent loads.

### 8. Batch Processing & Deadlock Prevention
- **TransactionProcessor**: Manages task execution using a 4-thread pool (`Executors.newFixedThreadPool(4)`), complete with orderly shutdown (`awaitTermination`).
- **Producer-Consumer Coordination**: `TransactionBuffer` implements a thread-safe bounded array buffer using synchronized `wait()` and `notify()` monitors.
- **Deadlock Resolution**: Demonstrates how opposite-order multi-account transfers cause thread deadlocks, and resolves the issue by consistently acquiring locks based on alphanumeric `accountNumber` order.

---

## Practical-Wise Implementation

| Practical | Academic Focus / Key Concepts | Actual Implemented Functionality | Status |
| :---: | :--- | :--- | :---: |
| **01** | JVM, JDK Setup, Console Menu, Enums, Records | `BankInfo` record header, `MenuOption` enum, Scanner-driven menu loop, switch expression placeholders | **Completed & Verified** |
| **02** | Encapsulation, Constructors, Access Modifiers, Static Members | `Customer` with `CUST###` auto-generation, `Account` with `AC####` generator, private fields, encapsulated `deposit()` / `withdraw()` | **Completed & Verified** |
| **03** | Object Class Methods, Equality Contract, Nested Classes | `toString()`, `equals()` and `hashCode()` contract on account numbers, static nested `Address`, `Cloneable` implementation | **Completed & Verified** |
| **04** | Regular Expressions, Strings, Records, Tokenization | `Validator` (Mobile, Email, PAN, IFSC), `TransactionType` enum, `Command` record, `CommandParser`, `StatementFormatter` | **Completed & Verified** |
| **05** | Abstract Classes, Inheritance, Runtime Polymorphism | Abstract `Account`, concrete subclasses `SavingsAccount`, `CurrentAccount`, `FixedDepositAccount`, dynamic dispatch on `interestRate()` | **Completed & Verified** |
| **06** | Interfaces, Default Methods, Lambdas, Packages, Runnable JAR | `Transactable`, `InterestBearing`, `WithdrawRule` (lambda & anonymous class), `Premium` marker, modular packaging, `minibank.jar` | **Completed & Verified** |
| **07** | Custom Annotations, Reflection API, Metadata Validation | `@Id`, `@Positive`, `@MaxLength`, field reflection via `AnnotationValidator`, runtime validation of invalid negative balances | **Completed & Verified** |
| **08** | Exception Hierarchy, Checked Exceptions, Resource Management | `BankException`, `InsufficientFundsException` (with shortfall), `InvalidAmountException`, `AccountNotFoundException`, `BankSession` (`AutoCloseable`) | **Completed & Verified** |
| **09** | Concurrency, Thread Lifecycle, Race Conditions, Synchronization | `AccountWorker`, 10 concurrent threads, `join()` lifecycle inspection, synchronized thread-safe deposit balance verification | **Completed & Verified** |
| **10** | Thread Pools, Inter-Thread Communication, Deadlock Avoidance | `TransactionProcessor` (4-thread pool), `TransactionBuffer` (`wait()`/`notify()`), safe transfers with ordered account locking | **Completed & Verified** |
| **11** | Object Serialization & NIO File Logging | File persistence (`accounts.dat`), NIO transaction logs, end-of-day summary reports | *Upcoming Milestone* |
| **12** | Java Collections Framework & Thread-Safe Maps | `AccountStore` (`HashMap`, `ArrayList`, `ConcurrentHashMap`), `Comparable` / `Comparator` sorting | *Upcoming Milestone* |
| **13** | Generics, Bounded Types & Generic Repository | Generic `Repository<T>` with reflective `@Id` resolution, `@SafeVarargs`, bounded wildcards | *Upcoming Milestone* |
| **14** | Layered Architecture, Integration & Unit Testing | Integrated `BankService` wiring UI, repository, validation, and JUnit test suite | *Upcoming Milestone* |

---

## Technology Stack

- **Platform**: Java Standard Edition (Java SE)
- **JDK Version**: OpenJDK / Oracle JDK 21 (`jdk-21.0.11`)
- **Language Level**: Java 17+ features (Records, Switch Expressions, Pattern Matching, Sealed/Abstract hierarchies)
- **Architecture**: Modular Package Organization (`model`, `util`, `service`, `exception`, `model.annotation`)
- **Dependencies**: 100% Pure Java Standard Library (`java.lang`, `java.util`, `java.util.concurrent`, `java.lang.reflect`, `java.util.regex`) — No external dependencies required.
- **Build Tools**: Standard JDK CLI toolchain (`javac`, `jar`, `java`)
- **Target OS**: Cross-platform (Windows, Linux, macOS)

---

## Project Architecture

```
                  +----------------------------------------------+
                  |                 Entry Points                 |
                  |  service.MiniBank (JAR)  |  service.Main     |
                  +----------------------+-----------------------+
                                         |
     +-----------------------------------+-----------------------------------+
     |                                   |                                   |
     v                                   v                                   v
+--------------------------+  +--------------------------+  +--------------------------+
|      service Package     |  |       util Package       |  |     exception Package    |
| - MiniBank (Console Menu)|  | - Validator (Regex)      |  | - BankException (Base)   |
| - TransactionProcessor   |  | - CommandParser          |  | - InsufficientFundsEx    |
| - AccountWorker          |  | - StatementFormatter     |  | - InvalidAmountException |
| - TransactionBuffer      |  | - AnnotationValidator    |  | - AccountNotFoundEx      |
+------------+-------------+  | - BankSession            |  +--------------------------+
             |                +--------------------------+
             v
+--------------------------------------------------------+
|                      model Package                     |
| - Account (Abstract Base)                              |
|   |-- SavingsAccount (implements Premium)              |
|   |-- CurrentAccount                                   |
|   `-- FixedDepositAccount                              |
| - Customer & Customer.Address (implements Cloneable)   |
| - Transactable, InterestBearing (Interfaces)           |
| - WithdrawRule (Functional Interface)                  |
| - Premium (Marker Interface)                           |
| - Command, BankInfo (Records)                          |
| - TransactionType, MenuOption (Enums)                  |
|                                                        |
|   +------------------------------------------------+   |
|   |            model.annotation Package            |   |
|   |  - @Id  |  - @Positive  |  - @MaxLength        |   |
|   +------------------------------------------------+   |
+--------------------------------------------------------+
```

### Package Responsibilities

- **`model`**: Contains core domain entities, data models, records, enums, and behavioral interfaces (`Account`, `Customer`, `Transactable`, `InterestBearing`).
- **`model.annotation`**: Contains custom compile- and runtime-retained annotations (`@Id`, `@Positive`, `@MaxLength`).
- **`exception`**: Contains the strongly-typed checked exception hierarchy for banking operations.
- **`util`**: Contains stateless utility classes for boundary string validation, command parsing, statement formatting, reflection validation, and session resource management.
- **`service`**: Orchestrates application logic, user interface menus, multithreaded workers, thread pool processing, and synchronization coordination.

---

## Project Structure

```
MINI BANK/
├── Manifest.txt                         # JAR manifest specifying Main-Class: service.MiniBank
├── minibank.jar                         # Pre-compiled runnable JAR archive
│
├── model/                               # Domain entities, records, enums & interfaces
│   ├── Account.java                     # Abstract base account implementing Transactable & InterestBearing
│   ├── BankInfo.java                    # Immutable record for bank metadata header
│   ├── Command.java                     # Immutable record representing parsed banking commands
│   ├── CurrentAccount.java              # Account subclass with overdraft support
│   ├── Customer.java                    # Customer entity with static nested Address & clone support
│   ├── FixedDepositAccount.java         # Account subclass with locked balance & high interest
│   ├── InterestBearing.java             # Interface with default yearly/monthly interest calculation
│   ├── MenuOption.java                  # Enum defining valid menu actions
│   ├── Premium.java                     # Marker interface for high-tier accounts
│   ├── SavingsAccount.java              # Account subclass with minBalance constraint
│   ├── Transactable.java                # Interface defining deposit and withdraw contracts
│   ├── TransactionType.java             # Enum for DEPOSIT, WITHDRAW, and TRANSFER
│   ├── WithdrawRule.java                # Functional interface for dynamic withdrawal policies
│   └── annotation/                      # Custom Java annotations
│       ├── Id.java                      # Primary key marker annotation (@Target FIELD, RUNTIME)
│       ├── MaxLength.java               # String length validation annotation
│       └── Positive.java                # Numeric positivity validation annotation
│
├── exception/                           # Custom checked banking exception hierarchy
│   ├── AccountNotFoundException.java    # Thrown when transfer target does not exist
│   ├── BankException.java               # Base checked exception for domain errors
│   ├── InsufficientFundsException.java  # Thrown when balance < requested amount; carries shortfall
│   └── InvalidAmountException.java      # Thrown when transaction amounts are <= 0
│
├── util/                                # Utility, validation and formatting engines
│   ├── AnnotationValidator.java         # Reflection engine that validates @Positive & @MaxLength
│   ├── BankSession.java                 # AutoCloseable resource for try-with-resources verification
│   ├── CommandParser.java               # Tokenizer converting command lines into Command records
│   ├── StatementFormatter.java          # StringBuilder formatter producing account statements
│   └── Validator.java                   # Pre-compiled regex patterns (Mobile, Email, PAN, IFSC)
│
├── service/                             # Concurrency, orchestration and entry-point drivers
│   ├── AccountWorker.java               # Runnable worker executing concurrent account deposits
│   ├── Main.java                        # Comprehensive verification suite (Practicals 1–10)
│   ├── MiniBank.java                    # Interactive console application (Main-Class for JAR)
│   ├── TransactionBuffer.java           # Thread-safe bounded buffer using wait() and notify()
│   └── TransactionProcessor.java        # Thread-pool manager (4 threads) & deadlock-free transfer logic
│
└── [Legacy Root Files]                  # Retained in root for backward compatibility with Practicals 1-5
    ├── Account.java, SavingsAccount.java, CurrentAccount.java, FixedDepositAccount.java
    ├── Customer.java, Command.java, CommandParser.java, StatementFormatter.java
    ├── Validator.java, TransactionType.java, Driver.java, Main.java, MiniBank.java
```

---

## Prerequisites

- **Java Development Kit (JDK)**: Version 17 or higher (JDK 21 recommended).
- **Environment Variable**: Ensure `JAVA_HOME` is configured and `java` / `javac` are available in your system path.
  ```powershell
  java -version
  javac -version
  ```

---

## Installation and Setup

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/harshkunpara/MiniBank.git
   cd "MiniBank"
   ```

2. **Open in VS Code**:
   Launch Visual Studio Code in the project directory:
   ```powershell
   code .
   ```
   *(Ensure the "Extension Pack for Java" by Microsoft is installed for syntax highlighting and running within the IDE).*

---

## Compilation and Execution

All commands below are formatted for Windows PowerShell and standard command shells.

### 1. Compile the Complete Package Architecture (Practicals 6–10)
```powershell
javac model/*.java model/annotation/*.java exception/*.java util/*.java service/*.java
```

### 2. Run the Full Practical Verification Suite
Executes the comprehensive automated verification driver covering all requirements from Practical 1 through Practical 10:
```powershell
java service.Main
```

### 3. Run the Interactive Console Application Directly
Launches the interactive MiniBank console banking shell:
```powershell
java service.MiniBank
```

### 4. Build the Runnable JAR File
Packages all compiled packages into an executable standalone JAR file:
```powershell
& "C:\Program Files\Java\jdk-21.0.11\bin\jar.exe" cfm minibank.jar Manifest.txt model exception util service
```
*(If `jar` is in your system PATH, you can run `jar cfm minibank.jar Manifest.txt model exception util service` directly).*

### 5. Execute the Runnable JAR
```powershell
java -jar minibank.jar
```

### 6. Run Legacy Root Practice Drivers (Practicals 1–5 Compatibility)
To verify that root-level files maintain backward compatibility:
```powershell
javac *.java
java Main
java Driver
```

---

## Usage Guide

### Interactive Console Mode
When launched via `java -jar minibank.jar` or `java service.MiniBank`, the user is greeted with the bank header and an interactive navigation menu:

```
BankInfo[name=MiniBank, branch=Dhrangadhra]

===== MINI BANK OF DHRANGADHRA=====
1. Open Account
2. Deposit
3. Withdraw
4. Transfer
5. Exit
Enter choice:
```

### Banking Command Syntax
The `CommandParser` utility supports processing tokenized banking directives:
```
<TRANSACTION_TYPE> <ACCOUNT_NUMBER> <AMOUNT>
```
**Examples:**
- `DEPOSIT AC0001 500`
- `WITHDRAW AC0002 1500`
- `TRANSFER AC0001 250`

---

## Sample Output

Below is the verbatim terminal output generated by executing the automated test suite (`java service.Main`):

```text
==================================================
   MINIBANK PRACTICAL SUITE (PRACTICAL 1 - 10)   
==================================================

>>> [PRACTICAL 1 - 5] Basic Model, Accounts & Validation
Bank Header : BankInfo[name=MiniBank, branch=Dhrangadhra]
Customer    : CUST101 | Riya | riya@gmail.com | 9876543210 | 123 Station Rd, Rajkot - 360001
Account: AC0001 | Riya | 5000 | Interest Rate: 4.0%
Account: AC0002 | Harsh | 10000 | Interest Rate: 0.0%
Account: AC0003 | Amit | 20000 | Interest Rate: 7.0%
Validator Mobile ('9876543210'): true
Validator Email ('riya@gmail.com'): true
Validator PAN ('ABCDE1234F'): true
Validator IFSC ('SBIN0001234'): true
Parsed Command: Type=DEPOSIT, Acc=AC0001, Amount=500
----------------------------
Account Number : AC0001
Owner Name     : Riya
Balance        : 5000
Active         : true
----------------------------

>>> [PRACTICAL 6] Interfaces, Default Methods & Lambdas
Transactable & InterestBearing: Riya Yearly Interest = 200.0
InterestBearing Monthly Interest = 16.666666666666668
WithdrawRule (Anonymous) for 2000 on balance 5000: true
WithdrawRule (Lambda) for 2000 on balance 5000: true
Is SavingsAccount marked Premium? true

>>> [PRACTICAL 7] Annotations & Reflection Validator
Validation on valid account error count: 0
Validation on account with negative balance (-100):
  Error: must be > 0

>>> [PRACTICAL 8] Exception Hierarchy & Try-With-Resources
[BankSession] Session SES-001 opened.
Current balance: 1000
Attempting withdrawal of 5000 (exceeds balance 1000)...
[BankSession] Session SES-001 closed safely.
Caught InsufficientFundsException: Insufficient funds: short by 4000 (Shortfall: 4000)
Finally block executed. Balance remains: 1000
Caught expected exception on negative deposit: Deposit amount must be positive: -200

>>> [PRACTICAL 9] Multithreading, Thread Lifecycle & Synchronization
Worker-1 initial state: NEW
Worker-2 initial state: NEW
Worker-3 initial state: NEW
Worker-4 initial state: NEW
Worker-5 initial state: NEW
Worker-6 initial state: NEW
Worker-7 initial state: NEW
Worker-8 initial state: NEW
Worker-9 initial state: NEW
Worker-10 initial state: NEW
Worker-1 completed with state: TERMINATED
Worker-2 completed with state: TERMINATED
Worker-3 completed with state: TERMINATED
Worker-4 completed with state: TERMINATED
Worker-5 completed with state: TERMINATED
Worker-6 completed with state: TERMINATED
Worker-7 completed with state: TERMINATED
Worker-8 completed with state: TERMINATED
Worker-9 completed with state: TERMINATED
Worker-10 completed with state: TERMINATED
Final balance after 10 threads * 1000 deposits of 1: 10000 (Expected: 10000)

>>> [PRACTICAL 10] Thread Pool, Producer-Consumer & Deadlock Resolution
--- 10.1 Thread Pool with TransactionProcessor ---
Pool task 1 deposited 100 by pool-1-thread-1
Pool task 5 deposited 100 by pool-1-thread-1
Pool task 6 deposited 100 by pool-1-thread-1
Pool task 7 deposited 100 by pool-1-thread-1
Pool task 8 deposited 100 by pool-1-thread-1
Pool task 3 deposited 100 by pool-1-thread-3
Pool task 2 deposited 100 by pool-1-thread-2
Pool task 4 deposited 100 by pool-1-thread-4
Pool account balance after tasks: 5800 (Expected: 5800)

--- 10.2 Producer-Consumer with wait() / notify() ---
Produced: DEPOSIT 100
Consumed: DEPOSIT 100
Produced: DEPOSIT 200
Consumed: DEPOSIT 200
Produced: DEPOSIT 300
Produced: DEPOSIT 400
Consumed: DEPOSIT 300
Produced: DEPOSIT 500
Consumed: DEPOSIT 400
Consumed: DEPOSIT 500

--- 10.3 Deadlock Prevention (Consistent Lock Ordering) ---
Initial: Alice balance = 2000, Bob balance = 2000
Transfer Alice -> Bob (500) completed successfully.
Transfer Bob -> Alice (300) completed successfully.
Final: Alice balance = 1800, Bob balance = 2200

==================================================
   ALL PRACTICAL 6 - 10 REQUIREMENTS COMPLETED!   
==================================================
```

---

## Java Concepts Demonstrated

### 1. Encapsulation & Immutability
- All account state variables (`balance`, `accountNumber`, `ownerName`, `active`) are strictly `private`.
- Balances cannot be modified through arbitrary setters; modifications must pass through business validation logic inside `deposit()` and `withdraw()`.
- Identifiers (`accountNumber`, `customerNumber`) are declared `final`, preventing identity mutation after instantiation.
- Java Records (`BankInfo`, `Command`) provide compile-time immutable data carriers.

### 2. Class Hierarchy & Runtime Polymorphism
- `Account` serves as an abstract base class containing common attributes and abstract contracts (`interestRate()` and `canWithdraw()`).
- At runtime, calls to `acc.interestRate()` dynamically resolve to the appropriate subtype method (e.g., `SavingsAccount` returns `4.0`, `CurrentAccount` returns `0.0`, `FixedDepositAccount` returns `7.0`).

### 3. Interface Design & Functional Paradigms
- **Default Methods**: `InterestBearing` supplies concrete calculations (`yearlyInterest` and `monthlyInterest`) directly inside the interface contract without breaking existing implementers.
- **Marker Interfaces**: `Premium` contains zero methods and functions as a runtime metadata tag checked via pattern matching (`instanceof Premium`).
- **Lambdas & Anonymous Classes**: `WithdrawRule` illustrates how functional interfaces simplify passing business logic as data:
  ```java
  // Anonymous Inner Class
  WithdrawRule rule1 = new WithdrawRule() {
      @Override
      public boolean allow(Account account, long amount) {
          return account.getBalance() >= amount;
      }
  };

  // Lambda Expression
  WithdrawRule rule2 = (account, amount) -> account.getBalance() >= amount;
  ```

### 4. Custom Annotations & Reflection API
- Declarative constraints (`@Id`, `@Positive`, `@MaxLength`) are defined with `@Retention(RetentionPolicy.RUNTIME)` and `@Target(ElementType.FIELD)`.
- `AnnotationValidator` utilizes Java's Reflection API (`java.lang.reflect`) to inspect fields, override access restrictions (`field.setAccessible(true)`), read values dynamically, and enforce validation rules without hardcoding checks into the domain classes.

### 5. Robust Exception Handling Architecture
- Checked exceptions (`BankException`) enforce compile-time error handling.
- `InsufficientFundsException` carries domain state (`shortfall`), enabling detailed feedback (`"Short by: 4000"`).
- `AutoCloseable` interface coupled with `try-with-resources` guarantees deterministic teardown of resources (`BankSession`) regardless of whether exceptions are thrown or caught.

### 6. Thread Safety & Synchronization
- Shared mutable state in `Account` is protected using method-level `synchronized` declarations.
- Prevents dirty reads and write overwrites when multiple worker threads execute concurrent deposit/withdrawal cycles.

### 7. Thread Pools & Resource Reuse
- `TransactionProcessor` replaces expensive ad-hoc thread creation with a fixed worker pool of 4 threads (`Executors.newFixedThreadPool(4)`). Tasks are scheduled via `submit(Runnable)` and terminated via graceful shutdown.

### 8. Producer-Consumer Coordination
- Demonstrates low-level thread coordination using intrinsic object locks:
  - `wait()` releases the lock and suspends execution when buffers are full/empty.
  - `notify()` / `notifyAll()` wakes waiting threads once capacity or items become available.

### 9. Deadlock Dynamics and Resolution
- **The Problem**: When Thread 1 transfers $A \to B$ (locks A, waits for B) and Thread 2 transfers $B \to A$ (locks B, waits for A), a cyclic dependency deadlock occurs.
- **The Solution**: Consistent lock ordering. By comparing alphanumeric account numbers:
  ```java
  Account first = from.getAccountNumber().compareTo(to.getAccountNumber()) < 0 ? from : to;
  Account second = (first == from) ? to : from;
  synchronized (first) {
      synchronized (second) {
          from.withdraw(amount);
          to.deposit(amount);
      }
  }
  ```
  Both threads always acquire locks in the exact same sequence, eliminating cyclic dependencies and preventing deadlocks.

---

## Testing and Verification

The project includes an end-to-end automated test driver located in [`service.Main`](file:///c:/Users/harsh/OneDrive/Desktop/Java%20Practical/MINI%20BANK/service/Main.java). The suite verifies:

1. **Entity Lifecycle**: Correct instantiation of accounts, auto-incrementing ID formats, and customer address cloning.
2. **Regex Correctness**: Asserts boundary pass/fail conditions for mobile, email, PAN, and IFSC inputs.
3. **Command Processing**: Verifies token splitting, enum parsing, and number extraction.
4. **Interface Contract Adherence**: Validates identical behavior between anonymous classes and lambda expressions under `WithdrawRule`.
5. **Reflection Accuracy**: Tests that valid objects produce 0 validation errors while negative balances trigger `@Positive` error messages.
6. **Exception Triggers**: Confirms that overdraft attempts on savings accounts throw `InsufficientFundsException` with accurate shortfall values.
7. **Thread Safety Verification**: Spawns 10 threads each performing 1,000 deposits of 1 rupee against a shared account, proving the final balance equals exactly 10,000.
8. **Thread Pool Execution**: Submits concurrent batches to an `ExecutorService` and ensures orderly task completion.
9. **Producer-Consumer Integrity**: Verifies 5 sequential deposits pass through a bounded array buffer without data loss.
10. **Deadlock Freedom**: Executes simultaneous bidirectional transfers between accounts without hanging.

---

## Learning Outcomes

Upon inspecting and executing this project, students gain practical mastery over:
- **CO1**: Designing well-encapsulated, maintainable classes with controlled state access and immutable data records.
- **CO2**: Applying inheritance, dynamic method dispatch, interfaces, and packaging to structure large-scale Java applications.
- **CO3**: Implementing clean runtime metadata validation using reflection and designing structured, recoverable checked exception hierarchies.
- **CO4**: Writing safe, multithreaded concurrent software, managing execution pools, coordinating thread communications, and resolving concurrency pitfalls like race conditions and deadlocks.

---

## Current Limitations

- **Volatile Storage**: Account and transaction state is stored in memory; data resets upon process termination (persistent file storage is scheduled for Practical 11).
- **Console Interface**: User interactions are conducted exclusively via the standard console terminal; no graphical user interface (GUI) or web interface is currently implemented.
- **Single-Node Execution**: Synchronization mechanisms rely on JVM-local object monitors and are not distributed across multiple JVM instances.

---

## Future Improvements

The remaining practical sessions will extend MiniBank into an enterprise-grade backend service:

- **Practical 11 (File I/O & Serialization)**:
  - Implement `Serializable` across all domain entities with `serialVersionUID`.
  - Save and load account snapshots to `accounts.dat` using `ObjectOutputStream` and `ObjectInputStream`.
  - Append audit records to transaction log files using Java NIO (`Files.write`).
  - Generate end-of-day transaction summaries using `Files.newDirectoryStream` and `BufferedReader`.
- **Practical 12 (Collections Framework & Concurrency)**:
  - Migrate plain array storage to `HashMap<String, Account>` inside an `AccountStore`.
  - Maintain historical transaction ledgers in `ArrayList<String>`.
  - Implement `Comparable<Account>` and balance-based sorting using `Comparator`.
  - Adopt `ConcurrentHashMap` for high-concurrency shared state.
- **Practical 13 (Generics & Generic Repositories)**:
  - Create a generic `Repository<T>` with CRUD methods (`save`, `findById`, `findAll`, `delete`).
  - Read `@Id` keys generically using reflection.
  - Implement bounded type methods (e.g., `<N extends Number> double average(N[] values)`).
- **Practical 14 (Service Layer & Unit Testing)**:
  - Wire repository, validation, and exception handling into a unified `BankService`.
  - Connect the interactive console menu to execute live banking services.
  - Add a comprehensive suite of JUnit 5 `@Test` test cases.

---

## 🎓 Academic Context & Credits

| **Category** | **Details** |
|:---|:---|
| **Course** | Object-Oriented Programming (CEUE203) |
| **Institute** | Chandubhai S. Patel Institute of Technology (CSPIT) |
| **Department** | Information Technology (IT) |
| **Academic Year** | 2026–2027 |
| **Semester** | Semester 3 |
| **Student** | [Harsh Kunpara](https://github.com/harshkunpara) |
| **Project** | [MiniBank](https://github.com/harshkunpara/MiniBank) |
| **Repository** | `OOP-MiniBank-25IT048` |

---

## License & Usage

This project was developed for **educational and academic evaluation purposes** as part of the Object-Oriented Programming course.

- **Ownership:** Project materials remain subject to the rights of their respective owners.
- **Permitted Use:** Academic learning, demonstration, and evaluation.
- **Commercial Use:** Commercial use or redistribution requires appropriate authorization from the respective rights holders.

---

<div align="center">

** Built with Java | Driven by Learning**

*CHARUSAT · CSPIT · Information Technology*

</div>
