package implementations;

import java.util.NoSuchElementException;

import utilities.BSTreeADT;
import utilities.Iterator;
import java.util.Stack;
 

public class BSTree<E extends Comparable<? super E>> implements BSTreeADT<E>
{
	// attributes
	private BSTreeNode<E> root;

    public BSTree(){
        this.root = null;
    }

    public BSTree(E element){
        this.root = new BSTreeNode<E>(element);
    }
	
	@Override
	public BSTreeNode<E> getRoot() throws NullPointerException {
		if (root == null) throw new NullPointerException();
		return root;
	}

    // Calculate height
    public int calculateHeight(BSTreeNode<E> root){
        if(root == null){
            return 0;
        }
        int leftHeight = calculateHeight(root.getLeft());
        int rightHeight = calculateHeight(root.getRight());
        return 1 + Math.max(leftHeight,rightHeight);
    }

	@Override
	public int getHeight() {
		if(root == null) {
			return 0;
		}
		return this.calculateHeight(root);
    }
	
	public int calculateSize(BSTreeNode<E> root) {
		if(root == null) {
			return 0;
		}
		int leftSize = calculateSize(root.getLeft());
		int rightSize = calculateSize(root.getRight());
		return 1 + leftSize + rightSize;
	}

	@Override
	public int size() {
		if(root == null) {
			return 0;
		}
		return calculateSize(root);
	}

	@Override
	public boolean isEmpty() {
		return this.size() == 0;
	}

	@Override
	public void clear() {
		root = null;
	}

	public boolean recursiveContains(BSTreeNode<E> cur, E entry) {
		if(cur == null) return false;
		int comp = entry.compareTo(cur.getElement());
		
		if(comp == 0) return true;
		
		return comp < 0 ? recursiveContains(cur.getLeft(),entry) : recursiveContains(cur.getRight(),entry);
	}
	@Override
	public boolean contains(E entry) throws NullPointerException {
		if(entry == null) throw new NullPointerException();
		return recursiveContains(root, entry);
	}
	
	public BSTreeNode<E> recursiveSearch(BSTreeNode<E> cur, E entry){
		if(cur == null) return null;
		
		int comp = entry.compareTo(cur.getElement());
		if (comp== 0) return cur;
		return comp < 0 ? recursiveSearch(cur.getLeft(),entry) :recursiveSearch(cur.getRight(),entry);
	}
 
	@Override
	public BSTreeNode<E> search(E entry) throws NullPointerException {
		if(entry == null) throw new NullPointerException();
		return recursiveSearch(root,entry);
	}
	
	public boolean recursiveAdd(BSTreeNode<E> root, E entry) {
		int comparision = entry.compareTo(root.getElement());
		if(comparision <= 0) {
			if(root.getLeft() == null) {
				root.setLeft(new BSTreeNode<>(entry));
				return true;
			}
			return recursiveAdd(root.getLeft(),entry);
		}
		else {
			if (root.getRight() == null) {
				root.setRight(new BSTreeNode<>(entry));
				return true;
			}
			return recursiveAdd(root.getRight(),entry);
		}
	}

	@Override
	public boolean add(E newEntry) throws NullPointerException {
		if(newEntry == null) {
			throw new NullPointerException();
		}
		if(root == null) {
			root = new BSTreeNode<>(newEntry);
			return true;
		}
		return recursiveAdd(root, newEntry);
	}

	private BSTreeNode<E> recursiveRemoveMin(BSTreeNode<E> parent, BSTreeNode<E> current) {
	    if (current.getLeft() == null) {
	        parent.setLeft(current.getRight());
	        current.setLeft(null);
	        return current;
	    }
	    return recursiveRemoveMin(current, current.getLeft());
	}

	@Override
	public BSTreeNode<E> removeMin() {
		if (root == null) return null;
		if (root.getLeft() == null) {
	        BSTreeNode<E> oldRoot = root;
	        root = root.getRight();
	        return oldRoot;
	    }
	    return recursiveRemoveMin(root, root.getLeft());
	}

	@Override
	public BSTreeNode<E> removeMax() {
		if (root == null) return null;
	    if (root.getRight() == null) {
	        BSTreeNode<E> oldRoot = root;
	        root = root.getLeft();
	        return oldRoot;
	    }
	    
	    return recursiveRemoveMax(root, root.getRight());
	}

	private BSTreeNode<E> recursiveRemoveMax(BSTreeNode<E> parent, BSTreeNode<E> current) {
	    if (current.getRight() == null) {
	        parent.setRight(current.getLeft());
	        current.setLeft(null);
	        return current;
	    }
	    return recursiveRemoveMax(current, current.getRight());
	}

	@Override
	public Iterator<E> inorderIterator() {
		// left -> node -> right
		return new Iterator<E>() {
			BSTreeNode<E> cur = root;
			Stack<BSTreeNode<E>> travStack = new Stack<>();
			
			// Initializer block: find the first (leftmost) node
	        {
	            pushLeftPath(cur);
	        }
			private void pushLeftPath(BSTreeNode<E> node) {
	            while (node != null) {
	                travStack.push(node);
	                node = node.getLeft();
	            }
	        }
			@Override
			public boolean hasNext() {
				return !travStack.isEmpty();
			}
			@Override
			public E next() {
				if(!hasNext()) throw new NoSuchElementException();
				// The top of the stack is the next in-order node
	            BSTreeNode<E> node = travStack.pop();
	            E result = node.getElement();

	            // If there is a right subtree, we need to explore its leftmost path
	            if (node.getRight() != null) {
	                pushLeftPath(node.getRight());
	            }

	            return result;
			}	
		};
	}

	@Override
	public Iterator<E> preorderIterator() {
		// Node -> Left -> Right
		return new Iterator<E>() {
			BSTreeNode<E> cur = root;
			Stack<BSTreeNode<E>> travStack = new Stack<>();
			
			{
				if(cur!= null) travStack.push(cur);
			}
			
			
			@Override
			public boolean hasNext() {
				return !travStack.isEmpty();
			}
			
			@Override
			public E next() {
				if(!hasNext()) throw new NoSuchElementException();
				
				BSTreeNode<E> node = travStack.pop();
				E result = node.getElement();
				
				if(node.getRight()!=null) travStack.push(node.getRight());
				if(node.getLeft() != null) travStack.push(node.getLeft());
				
				return result;
				
			}
		};
	}

	@Override
	public Iterator<E> postorderIterator() {
		return new Iterator<E>() {
			BSTreeNode<E> cur = root;
			BSTreeNode<E> lastVisited = null;
			Stack<BSTreeNode<E>> travStack = new Stack<>();

			
			@Override
			public boolean hasNext() {
				return !travStack.isEmpty() || cur != null;
			}
			@Override
	        public E next() {
				if (!hasNext()) throw new NoSuchElementException();
	            while (hasNext()) {
	                if (cur != null) {
	                    travStack.push(cur);
	                    cur = cur.getLeft();
	                } else {
	                    BSTreeNode<E> peekNode = travStack.peek();
	                    // If right child exists and we haven't come back from it yet
	                    if (peekNode.getRight() != null && lastVisited != peekNode.getRight()) {
	                        cur = peekNode.getRight();
	                    } else {
	                    	travStack.pop();
	                        lastVisited = peekNode;
	                        return peekNode.getElement();
	                    }
	                }
	            }
				throw new NoSuchElementException();
	        }
		};	
	}
}
