package org.subclass.logic.proof.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import org.subclass.logic.proof.typed.Cardinality;
import org.subclass.logic.proof.typed.Formula;
import org.subclass.logic.proof.typed.Proof;
import org.subclass.logic.proof.typed.ProofNode;
import org.subclass.logic.proof.typed.Sequent;
import org.subclass.logic.rules.RuleDescriptor;
import org.subclass.logic.rules.RuleRegistry;
import org.subclass.logic.rules.axiom.Axiom;
import org.subclass.logic.rules.conjunction.AndLeft;
import org.subclass.logic.rules.conjunction.AndRight;
import org.subclass.logic.rules.cut.Cut;
import org.subclass.logic.rules.disjunction.OrLeft;
import org.subclass.logic.rules.disjunction.OrRight;
import org.subclass.logic.rules.implication.ImpliesLeft;
import org.subclass.logic.rules.implication.ImpliesRight;
import org.subclass.logic.rules.negation.NotLeft;
import org.subclass.logic.rules.negation.NotRight;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Serialization / deserialization of {@link Proof} trees in three formats:
 * JSON (Jackson), XML (JAXB), and S-expressions (hand-written).
 *
 * Round-trip property: for every typed proof {@code p} and every format
 * {@code f}, {@code read(f, write(f, p))} is structurally equal to {@code p}
 * (as witnessed by {@link org.subclass.logic.proof.typed.Sequent#equals} and
 * the rule-node record equality).
 */
public final class ProofIO {

    private ProofIO() {}

    private static final ObjectMapper JSON = new ObjectMapper()
        .enable(SerializationFeature.INDENT_OUTPUT);

    // ---------- Public API -------------------------------------------------

    public static String writeJson(Proof<?, ?> proof) {
        try {
            return JSON.writeValueAsString(toDto(proof));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize proof to JSON", e);
        }
    }

    public static Proof<?, ?> readJson(String json) {
        try {
            ProofDto dto = JSON.readValue(json, ProofDto.class);
            return fromDto(dto);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse proof from JSON", e);
        }
    }

    public static String writeXml(Proof<?, ?> proof) {
        try {
            JAXBContext ctx = JAXBContext.newInstance(ProofDto.class, SequentDto.class,
                FormulaDto.class, FormulaDto.Atom.class, FormulaDto.Not.class,
                FormulaDto.And.class, FormulaDto.Or.class, FormulaDto.Implies.class);
            Marshaller m = ctx.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            StringWriter sw = new StringWriter();
            jakarta.xml.bind.JAXBElement<ProofDto> root =
                new jakarta.xml.bind.JAXBElement<>(
                    new javax.xml.namespace.QName("proof"),
                    ProofDto.class,
                    toDto(proof));
            m.marshal(root, sw);
            return sw.toString();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize proof to XML", e);
        }
    }

    public static Proof<?, ?> readXml(String xml) {
        try {
            JAXBContext ctx = JAXBContext.newInstance(ProofDto.class, SequentDto.class,
                FormulaDto.class, FormulaDto.Atom.class, FormulaDto.Not.class,
                FormulaDto.And.class, FormulaDto.Or.class, FormulaDto.Implies.class);
            Unmarshaller u = ctx.createUnmarshaller();
            jakarta.xml.bind.JAXBElement<ProofDto> el =
                u.unmarshal(new javax.xml.transform.stream.StreamSource(new StringReader(xml)),
                    ProofDto.class);
            return fromDto(el.getValue());
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse proof from XML", e);
        }
    }

    public static String writeSExpr(Proof<?, ?> proof) {
        StringBuilder sb = new StringBuilder();
        writeSExpr(sb, toDto(proof));
        return sb.toString();
    }

    public static Proof<?, ?> readSExpr(String sexpr) {
        SExprParser parser = new SExprParser(sexpr);
        ProofDto dto = parser.parseProof();
        parser.expectEof();
        return fromDto(dto);
    }

    // ---------- Proof <-> DTO conversion -----------------------------------

    public static ProofDto toDto(Proof<?, ?> proof) {
        if (!(proof instanceof ProofNode<?, ?> node)) {
            throw new IllegalArgumentException(
                "Cannot serialize non-ProofNode Proof: " + proof.getClass().getName());
        }
        ProofDto dto = new ProofDto();
        dto.rule = node.ruleName();
        dto.conclusion = toDto(node.conclusion());
        dto.premises = new ArrayList<>();
        for (Proof<?, ?> p : node.premises()) {
            dto.premises.add(toDto(p));
        }
        if (node instanceof Axiom<?, ?> ax) {
            dto.formula = toDto(ax.formula());
        } else if (node instanceof Cut<?, ?> cut) {
            dto.formula = toDto(cut.cutFormula());
        }
        return dto;
    }

    public static Proof<?, ?> fromDto(ProofDto dto) {
        if (dto == null || dto.rule == null) {
            throw new IllegalArgumentException("ProofDto missing rule name");
        }
        RuleDescriptor desc = RuleRegistry.instance().find(dto.rule)
            .orElseThrow(() -> new IllegalArgumentException(
                "Unknown rule: " + dto.rule));
        return reconstruct(dto, desc);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <L extends Cardinality, R extends Cardinality>
    Proof<L, R> reconstruct(ProofDto dto, RuleDescriptor desc) {
        Sequent<L, R> conclusion = dto.conclusion == null
            ? null
            : (Sequent<L, R>) fromDto(dto.conclusion);
        List<ProofDto> pr = dto.premises == null ? List.of() : dto.premises;

        return switch (dto.rule) {
            case "Axiom" -> {
                if (dto.formula == null) {
                    throw new IllegalArgumentException("Axiom requires formula");
                }
                yield new Axiom<>(fromDto(dto.formula));
            }
            case "Cut" -> {
                if (dto.formula == null || pr.size() != 2) {
                    throw new IllegalArgumentException("Cut requires formula and 2 premises");
                }
                yield new Cut<>(fromDto(dto.formula),
                    (Proof<L, R>) fromDto(pr.get(0)),
                    (Proof<L, R>) fromDto(pr.get(1)),
                    conclusion);
            }
            case "AndLeft" -> new AndLeft<>((Proof<L, R>) fromDto(pr.get(0)), conclusion);
            case "AndRight" -> new AndRight<>(
                (Proof<L, R>) fromDto(pr.get(0)),
                (Proof<L, R>) fromDto(pr.get(1)),
                conclusion);
            case "OrLeft" -> new OrLeft<>(
                (Proof<L, R>) fromDto(pr.get(0)),
                (Proof<L, R>) fromDto(pr.get(1)),
                conclusion);
            case "OrRight" -> new OrRight<>((Proof<L, R>) fromDto(pr.get(0)), conclusion);
            case "ImpliesLeft" -> new ImpliesLeft<>(
                (Proof<L, R>) fromDto(pr.get(0)),
                (Proof<L, R>) fromDto(pr.get(1)),
                conclusion);
            case "ImpliesRight" -> new ImpliesRight<>((Proof<L, R>) fromDto(pr.get(0)), conclusion);
            case "NotLeft" -> new NotLeft<>((Proof<L, R>) fromDto(pr.get(0)), conclusion);
            case "NotRight" -> new NotRight<>((Proof<L, R>) fromDto(pr.get(0)), conclusion);
            default -> throw new IllegalArgumentException(
                "Rule registered but unknown to deserializer: " + desc.name());
        };
    }

    // ---------- Sequent / Formula <-> DTO ---------------------------------

    public static SequentDto toDto(Sequent<?, ?> seq) {
        List<FormulaDto> ante = new ArrayList<>();
        for (Formula f : seq.antecedent()) ante.add(toDto(f));
        List<FormulaDto> succ = new ArrayList<>();
        for (Formula f : seq.succedent()) succ.add(toDto(f));
        return new SequentDto(ante, succ);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Sequent fromDto(SequentDto dto) {
        List<Formula> ante = new ArrayList<>();
        List<FormulaDto> a = dto.antecedent == null ? List.of() : dto.antecedent;
        for (FormulaDto f : a) ante.add(fromDto(f));
        List<Formula> succ = new ArrayList<>();
        List<FormulaDto> s = dto.succedent == null ? List.of() : dto.succedent;
        for (FormulaDto f : s) succ.add(fromDto(f));
        return new Sequent(ante, succ);
    }

    public static FormulaDto toDto(Formula f) {
        if (f instanceof Formula.Atom a) return new FormulaDto.Atom(a.name());
        if (f instanceof Formula.Not n) return new FormulaDto.Not(toDto(n.formula()));
        if (f instanceof Formula.And x) return new FormulaDto.And(toDto(x.left()), toDto(x.right()));
        if (f instanceof Formula.Or x) return new FormulaDto.Or(toDto(x.left()), toDto(x.right()));
        if (f instanceof Formula.Implies i) return new FormulaDto.Implies(toDto(i.antecedent()), toDto(i.consequent()));
        throw new IllegalArgumentException("Unknown formula type: " + f.getClass());
    }

    public static Formula fromDto(FormulaDto d) {
        if (d instanceof FormulaDto.Atom a) return new Formula.Atom(a.name);
        if (d instanceof FormulaDto.Not n) return new Formula.Not(fromDto(n.formula));
        if (d instanceof FormulaDto.And x) return new Formula.And(fromDto(x.left), fromDto(x.right));
        if (d instanceof FormulaDto.Or x) return new Formula.Or(fromDto(x.left), fromDto(x.right));
        if (d instanceof FormulaDto.Implies i) return new Formula.Implies(fromDto(i.antecedent), fromDto(i.consequent));
        throw new IllegalArgumentException("Unknown formula DTO: " + d);
    }

    // ---------- S-expression writer ---------------------------------------

    private static void writeSExpr(StringBuilder sb, ProofDto p) {
        sb.append("(proof ").append(quote(p.rule));
        if (p.formula != null) {
            sb.append(" :formula ");
            writeSExpr(sb, p.formula);
        }
        sb.append(" :conclusion ");
        writeSExpr(sb, p.conclusion);
        sb.append(" :premises (");
        boolean first = true;
        for (ProofDto c : p.premises) {
            if (!first) sb.append(' ');
            writeSExpr(sb, c);
            first = false;
        }
        sb.append("))");
    }

    private static void writeSExpr(StringBuilder sb, SequentDto s) {
        sb.append("(sequent (");
        boolean first = true;
        for (FormulaDto f : s.antecedent) {
            if (!first) sb.append(' ');
            writeSExpr(sb, f);
            first = false;
        }
        sb.append(") (");
        first = true;
        for (FormulaDto f : s.succedent) {
            if (!first) sb.append(' ');
            writeSExpr(sb, f);
            first = false;
        }
        sb.append("))");
    }

    private static void writeSExpr(StringBuilder sb, FormulaDto f) {
        if (f instanceof FormulaDto.Atom a) {
            sb.append("(atom ").append(quote(a.name)).append(')');
        } else if (f instanceof FormulaDto.Not n) {
            sb.append("(not "); writeSExpr(sb, n.formula); sb.append(')');
        } else if (f instanceof FormulaDto.And x) {
            sb.append("(and "); writeSExpr(sb, x.left); sb.append(' '); writeSExpr(sb, x.right); sb.append(')');
        } else if (f instanceof FormulaDto.Or x) {
            sb.append("(or "); writeSExpr(sb, x.left); sb.append(' '); writeSExpr(sb, x.right); sb.append(')');
        } else if (f instanceof FormulaDto.Implies i) {
            sb.append("(implies "); writeSExpr(sb, i.antecedent); sb.append(' '); writeSExpr(sb, i.consequent); sb.append(')');
        } else {
            throw new IllegalArgumentException("Unknown formula DTO: " + f);
        }
    }

    private static String quote(String s) {
        StringBuilder out = new StringBuilder("\"");
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '"' || c == '\\') out.append('\\');
            out.append(c);
        }
        return out.append('"').toString();
    }

    // ---------- S-expression parser ---------------------------------------

    private static final class SExprParser {
        private final String src;
        private int i;

        SExprParser(String src) { this.src = src; this.i = 0; }

        void skipWs() {
            while (i < src.length() && Character.isWhitespace(src.charAt(i))) i++;
        }

        void expect(char c) {
            skipWs();
            if (i >= src.length() || src.charAt(i) != c) {
                throw new IllegalArgumentException(
                    "Expected '" + c + "' at position " + i + " in: " + src);
            }
            i++;
        }

        void expectEof() {
            skipWs();
            if (i < src.length()) {
                throw new IllegalArgumentException(
                    "Trailing characters at position " + i + ": " + src.substring(i));
            }
        }

        String parseKeyword() {
            skipWs();
            if (i >= src.length() || src.charAt(i) != ':') {
                throw new IllegalArgumentException("Expected keyword at position " + i);
            }
            i++;
            int start = i;
            while (i < src.length() && !Character.isWhitespace(src.charAt(i))
                && src.charAt(i) != '(' && src.charAt(i) != ')') i++;
            return src.substring(start, i);
        }

        String parseSymbol() {
            skipWs();
            int start = i;
            while (i < src.length() && !Character.isWhitespace(src.charAt(i))
                && src.charAt(i) != '(' && src.charAt(i) != ')') i++;
            return src.substring(start, i);
        }

        String parseString() {
            skipWs();
            expect('"');
            StringBuilder sb = new StringBuilder();
            while (i < src.length() && src.charAt(i) != '"') {
                char c = src.charAt(i++);
                if (c == '\\' && i < src.length()) c = src.charAt(i++);
                sb.append(c);
            }
            expect('"');
            return sb.toString();
        }

        ProofDto parseProof() {
            skipWs();
            expect('(');
            String head = parseSymbol();
            if (!"proof".equals(head)) {
                throw new IllegalArgumentException("Expected 'proof' head, got: " + head);
            }
            ProofDto p = new ProofDto();
            p.rule = parseString();
            while (true) {
                skipWs();
                if (peek() == ')') break;
                String kw = parseKeyword();
                switch (kw) {
                    case "formula" -> p.formula = parseFormula();
                    case "conclusion" -> p.conclusion = parseSequent();
                    case "premises" -> {
                        skipWs();
                        expect('(');
                        p.premises = new ArrayList<>();
                        while (true) {
                            skipWs();
                            if (peek() == ')') break;
                            p.premises.add(parseProof());
                        }
                        expect(')');
                    }
                    default -> throw new IllegalArgumentException("Unknown proof keyword: " + kw);
                }
            }
            expect(')');
            return p;
        }

        SequentDto parseSequent() {
            skipWs();
            expect('(');
            String head = parseSymbol();
            if (!"sequent".equals(head)) {
                throw new IllegalArgumentException("Expected 'sequent' head, got: " + head);
            }
            SequentDto s = new SequentDto();
            s.antecedent = parseFormulaList();
            s.succedent = parseFormulaList();
            expect(')');
            return s;
        }

        List<FormulaDto> parseFormulaList() {
            skipWs();
            expect('(');
            List<FormulaDto> out = new ArrayList<>();
            while (true) {
                skipWs();
                if (peek() == ')') break;
                out.add(parseFormula());
            }
            expect(')');
            return out;
        }

        FormulaDto parseFormula() {
            skipWs();
            expect('(');
            String head = parseSymbol();
            FormulaDto out = switch (head) {
                case "atom" -> new FormulaDto.Atom(parseString());
                case "not" -> new FormulaDto.Not(parseFormula());
                case "and" -> new FormulaDto.And(parseFormula(), parseFormula());
                case "or" -> new FormulaDto.Or(parseFormula(), parseFormula());
                case "implies" -> new FormulaDto.Implies(parseFormula(), parseFormula());
                default -> throw new IllegalArgumentException("Unknown formula head: " + head);
            };
            expect(')');
            return out;
        }

        char peek() {
            skipWs();
            return i < src.length() ? src.charAt(i) : '\0';
        }
    }
}
