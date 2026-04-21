package implementations;

import java.io.Serializable;

public class BSTreeNode<E> implements Serializable
{
	private static final long serialVersionUID = 1L;
	private E element;
	private BSTreeNode<E> left,right;

	
	public BSTreeNode(E e) {
		this.element = e;
		this.left= null;
		this.right = null;
	}
	
	/**
	 * @return the element
	 */
	public E getElement() {
		return element;
	}

	/**
	 * @param element the element to set
	 */
	public void setElement(E element) {
		this.element = element;
	}

	/**
	 * @return the leftChild
	 */
	public BSTreeNode<E> getLeft() {
		return left;
	}

	/**
	 * @param leftChild the leftChild to set
	 */
	public void setLeft(BSTreeNode<E> left) {
		this.left = left;
	}

	/**
	 * @return the rightChild
	 */
	public BSTreeNode<E> getRight() {
		return right;
	}

	/**
	 * @param rightChild the rightChild to set
	 */
	public void setRight(BSTreeNode<E> right) {
		this.right = right;
	}
	
}
