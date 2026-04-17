# Classical First-Order Theories: Proof-Theoretic Formalization

## 1. The Sequent Calculus $\mathbf{LK}$

### 1.1 Sequents

A **sequent** is an expression $\Gamma \vdash \Delta$ where:
- $\Gamma$ (the **antecedent**) is a finite multiset of formulas
- $\Delta$ (the **succedent**) is a finite multiset of formulas

The turnstile $\vdash$ separates assumptions (left) from conclusions (right).

*Variant:* If $\Gamma, \Delta$ are taken as **sets** (rather than multisets), Exchange and Contraction rules become redundant and may be omitted.

### 1.2 Structural Rules

$$\frac{\Gamma, A, B, \Gamma' \vdash \Delta}{\Gamma, B, A, \Gamma' \vdash \Delta}  (\text{XL}) \qquad \frac{\Gamma \vdash \Delta, A, B, \Delta'}{\Gamma \vdash \Delta, B, A, \Delta'}  (\text{XR})$$

$$\frac{\Gamma \vdash \Delta}{\Gamma, A \vdash \Delta}  (\text{WL}) \qquad \frac{\Gamma \vdash \Delta}{\Gamma \vdash \Delta, A}  (\text{WR})$$

$$\frac{\Gamma, A, A \vdash \Delta}{\Gamma, A \vdash \Delta}  (\text{CL}) \qquad \frac{\Gamma \vdash \Delta, A, A}{\Gamma \vdash \Delta, A}  (\text{CR})$$

### 1.3 Logical Rules

**Negation:**

$$\frac{\Gamma \vdash \Delta, A}{\Gamma, \neg A \vdash \Delta}  (\neg\text{L}) \qquad \frac{\Gamma, A \vdash \Delta}{\Gamma \vdash \Delta, \neg A}  (\neg\text{R})$$

**Conjunction:**

$$\frac{\Gamma, A \vdash \Delta}{\Gamma, A \land B \vdash \Delta}  (\land\text{L}_1) \qquad \frac{\Gamma, B \vdash \Delta}{\Gamma, A \land B \vdash \Delta}  (\land\text{L}_2) \qquad \frac{\Gamma \vdash \Delta, A \quad \Gamma \vdash \Delta, B}{\Gamma \vdash \Delta, A \land B}  (\land\text{R})$$

**Disjunction:**

$$\frac{\Gamma, A \vdash \Delta \quad \Gamma, B \vdash \Delta}{\Gamma, A \lor B \vdash \Delta}  (\lor\text{L}) \qquad \frac{\Gamma \vdash \Delta, A}{\Gamma \vdash \Delta, A \lor B}  (\lor\text{R}_1) \qquad \frac{\Gamma \vdash \Delta, B}{\Gamma \vdash \Delta, A \lor B}  (\lor\text{R}_2)$$

**Implication:**

$$\frac{\Gamma \vdash \Delta, A \quad \Gamma, B \vdash \Delta}{\Gamma, A \to B \vdash \Delta}  (\to\text{L}) \qquad \frac{\Gamma, A \vdash \Delta, B}{\Gamma \vdash \Delta, A \to B}  (\to\text{R})$$

**Universal Quantifier:**

$$\frac{\Gamma, A[t/x] \vdash \Delta}{\Gamma, \forall x.A \vdash \Delta}  (\forall\text{L})$$

where $t$ is free for $x$ in $A$ (no variable in $t$ becomes bound in $A[t/x]$).

$$\frac{\Gamma \vdash \Delta, A[y/x]}{\Gamma \vdash \Delta, \forall x.A}  (\forall\text{R})$$

where $y$ is an **eigenvariable**: $y$ is not free in the conclusion $\Gamma \vdash \Delta, \forall x.A$.

**Existential Quantifier:**

$$\frac{\Gamma, A[y/x] \vdash \Delta}{\Gamma, \exists x.A \vdash \Delta}  (\exists\text{L})$$

where $y$ is an **eigenvariable**: $y$ is not free in the conclusion $\Gamma, \exists x.A \vdash \Delta$.

$$\frac{\Gamma \vdash \Delta, A[t/x]}{\Gamma \vdash \Delta, \exists x.A}  (\exists\text{R})$$

where $t$ is free for $x$ in $A$.

### 1.4 Initial Sequents

For any formula $A$:

$$\Gamma, A \vdash A, \Delta$$

### 5.2 Cut Rule (Admissible)

$$\frac{\Gamma \vdash \Delta, A \quad \Gamma', A \vdash \Delta'}{\Gamma, \Gamma' \vdash \Delta, \Delta'}  (\text{Cut})$$

Gentzen's Hauptsatz: Cut is admissible in $\mathbf{LK}$ (eliminable from any derivation). 

## 3. Theory Extensions

A **theory** $T$ over signature $\Sigma$ is a structure $T = (\Sigma, R)$ extending $\mathbf{LK}$, where $R$ is a set of **non-logical rules**.

### 3.1 Non-Logical Rules

Each rule $r \in R$ has the form:
$$\frac{\Gamma_1 \vdash \Delta_1 \quad \cdots \quad \Gamma_n \vdash \Delta_n}{\Gamma \vdash \Delta} \quad (n \geq 0)$$

where:
- $\Gamma_i, \Gamma$ and $\Delta_i, \Delta$ are finite multisets of $\text{Form}(\Sigma)$
- When $n = 0$, $r$ is an **axiom sequent** (zero-premise rule)

**Forms permitted:**
- Multiple premises ($n \geq 1$)
- Multiple antecedent formulas (multiset $\Gamma$)
- Multiple succedent formulas (multiset $\Delta$)

Theories may include Cut explicitly in $R$ or remain Cut-free.

