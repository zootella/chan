package org.zootella.base.state;

import javax.swing.JDialog;

import org.junit.Test;

import org.zootella.base.state.OldReceive;
import org.zootella.base.state.Update;
import org.zootella.base.user.Dialog;

public class UpdateTest {
	
	@Test
	public void spinTest() {
		
		// this will produce a SpinException right away
		// then, when you close the box, the test will succeed
		new TestBox();
	}

	private class TestBox {
		
		public TestBox() {
			
			spin();
			
			JDialog dialog = Dialog.modal("Test Box");
			dialog.setVisible(true); // control sticks here while the dialog is open
		}
	}
	
	private void spin() {

		Parent parent = new Parent();
		parent.child.finished();
	}

	private class Parent {
		
		public Parent() {
			
			update = new Update();
			child = new Child(update);
		}
		
		public Update update;
		public Child child;
		
		private class MyReceive implements OldReceive {
			public void receive() {
				
				child.finished();
			}
		}
	}
	
	private class Child {
		
		public Child(Update update) {
			this.update = update;
		}
		
		private Update update;
		
		public void finished() {
			
			update.send();
		}
	}
}
