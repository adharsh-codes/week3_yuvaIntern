# Inventory Management System - Unit Testing & Debugging (Week 3)

Java 17+ | JUnit 5 | Maven

## Structure
```
src/main/java/com/inventory   Item, InventoryManager, custom exceptions
src/test/java/com/inventory   ItemTest (13 tests), InventoryManagerTest (40 tests)
docs/bug-evidence/            Raw test output for the 3 runs + the original buggy v1 source
run-tests.sh                  Maven-free runner (JUnit Console Launcher)
pom.xml                       Maven build (JUnit 5 + JaCoCo)
```

## Run the tests
**Maven:** `mvn test`   (coverage report: `mvn verify` -> `target/site/jacoco/index.html`)

**Without Maven:** download `junit-platform-console-standalone` and run
`./run-tests.sh path/to/junit-platform-console-standalone.jar`

**IDE:** open as a Maven project in IntelliJ / Eclipse / VS Code and run `src/test/java`.

## Debugging history (summary)
| Run | Source version | Result |
|-----|----------------|--------|
| 1 | Buggy v1 (8 seeded defects) | 32 passed, **21 failed** |
| 2 | After fix round 1 | 52 passed, **1 failed** (new defect exposed: non-atomic update) |
| 3 | After fix round 2 | **53 passed, 0 failed** |

See the Word report for the full analysis.
