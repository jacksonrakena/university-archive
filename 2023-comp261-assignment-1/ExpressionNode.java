/**
 * Represents a node that can be evaluated at runtime to receive a value of type T.
 *
 * Common implementations include
 * {@link ArithmeticExpressionNode} (implementing {@link ExpressionNode<Integer>})
 * and {@link ComparativeExpressionNode} (implementing {@link ExpressionNode<Boolean>}
 * @param <T>
 */
public interface ExpressionNode<T> extends Node, GenericExpressionNode {
    /**
     * Evaluates this node at runtime, producing a value of type {@link T} with
     * a valid robot context.
     */
    T evaluate(Robot robot);
}
