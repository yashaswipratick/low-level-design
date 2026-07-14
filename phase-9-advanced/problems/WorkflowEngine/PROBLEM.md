# Problem: Workflow Engine
> Domain: Platform / Orchestration | Difficulty: Staff | Est. Time: 90 min | Interview Frequency: ⭐ Common at Staff Level

---

## Problem Statement

Design a Workflow Engine that executes DAG-based (Directed Acyclic Graph) task pipelines.

Requirements:
1. A workflow is a DAG of tasks; tasks can depend on outputs of other tasks
2. Independent tasks execute in parallel; dependent tasks wait for their dependencies
3. Each task has a retry policy (max attempts, backoff strategy)
4. Failed tasks trigger compensating actions for already-completed tasks (Saga pattern)
5. Workflow state persists — can survive process restarts and continue execution
6. Workflow execution can be paused, resumed, and cancelled
7. Progress visibility: query current state of any workflow and any individual task

---

## Clarifying Questions to Ask

- Is task execution local (in-process) or distributed (across services)?
- Are workflow definitions static (compiled) or dynamic (defined at runtime via DSL/JSON)?
- What happens to in-flight tasks when the engine restarts?
- Is there a timeout per task or per workflow?
- Can a workflow call another workflow (sub-workflow)?
- Are human approval steps in scope?

---

## Key Concepts Required

- **DAG traversal** — topological sort to find execution order
- **Saga Pattern** — compensating transactions on failure
- **State persistence** — checkpoint current workflow state
- **Thread pool with `CompletableFuture`** — parallel task execution
- **Observer/Event-driven** — task completion triggers dependent tasks

---

## Class Design Starting Point

```
WorkflowDefinition
  ├── String workflowId
  ├── List<TaskDefinition> tasks
  └── List<TaskDependency> edges  // (from: taskA, to: taskB) = B depends on A

TaskDefinition
  ├── String taskId
  ├── TaskExecutor executor       // the actual business logic
  ├── RetryPolicy retryPolicy
  └── TaskDefinition compensator  // rollback action if saga unwinds

WorkflowInstance
  ├── String instanceId
  ├── WorkflowDefinition definition
  ├── Map<String, TaskState> taskStates
  ├── WorkflowStatus status       // RUNNING, PAUSED, COMPLETED, FAILED, COMPENSATING
  └── Map<String, Object> context // shared data passed between tasks

TaskExecutor (interface)
  └── TaskResult execute(TaskContext context) throws TaskException

RetryPolicy
  ├── int maxAttempts
  ├── Duration initialDelay
  └── BackoffStrategy backoff     // FIXED, EXPONENTIAL, JITTER

WorkflowEngine
  ├── WorkflowInstance start(WorkflowDefinition definition, Map<String, Object> input)
  ├── void pause(String instanceId)
  ├── void resume(String instanceId)
  └── WorkflowStatus getStatus(String instanceId)
```

---

## Your Task

1. `WorkflowEngine` with DAG-based execution (topological sort → parallel execution)
2. `RetryDecorator` around any `TaskExecutor` with exponential backoff
3. `SagaCoordinator` — on task failure, trigger compensators for completed tasks in reverse order
4. `WorkflowStateRepository` — persist and reload workflow state (in-memory for now, interface for DB)
5. Parallel execution using `CompletableFuture` and `ExecutorService`
6. Implement in `src/main/java/com/lld/phase9/workflow/`

---

## Edge Cases

- Circular dependency in workflow definition — detect at registration, not at runtime
- Task times out after 30s — should it count as failure and trigger retry?
- Compensation task itself fails — what happens?
- Workflow paused while tasks are mid-execution — in-flight tasks complete, no new tasks start
- Two workflow engines pick up the same instance after restart — double execution
- Very long workflow: 500 tasks — memory / state management

---

## Saga Pattern Deep-Dive

```
Order Workflow:
  1. ReserveInventory     → compensate: ReleaseInventory
  2. ChargePayment        → compensate: RefundPayment
  3. AllocateFulfillment  → compensate: DeallocateFulfillment
  4. NotifyCustomer       → compensate: SendCancellationNotice

Failure at step 3:
  Execute: DeallocateFulfillment (step 3 compensate) — N/A (step 3 failed)
  Execute: RefundPayment (step 2 compensate)
  Execute: ReleaseInventory (step 1 compensate)
  → System is back to consistent state
```

---

## Trade-off Discussion Points

| Design Decision | Option A | Option B |
|-----------------|----------|----------|
| State storage | In-memory (fast, lost on restart) | DB/Redis (durable, slower) |
| Orchestration vs Choreography | Centralized engine (easier to trace) | Event-driven sagas (decoupled) |
| Parallel execution | Thread pool | Reactive (WebFlux/Reactor) |
| Retry granularity | Per-task | Per-workflow step group |
