package ch.openech.flottesohle.frontend;

import java.util.ArrayList;
import java.util.List;

import org.minimalj.frontend.Frontend;
import org.minimalj.frontend.Frontend.IComponent;
import org.minimalj.frontend.Frontend.Input;
import org.minimalj.frontend.Frontend.SwitchComponent;
import org.minimalj.frontend.action.Action;
import org.minimalj.frontend.action.ActionGroup;
import org.minimalj.frontend.editor.Editor.NewObjectEditor;
import org.minimalj.frontend.form.Form;
import org.minimalj.frontend.form.element.AbstractFormElement;
import org.minimalj.frontend.form.element.ComboBoxFormElement;
import org.minimalj.frontend.form.element.Enable;
import org.minimalj.model.Keys;
import org.minimalj.model.Rendering;
import org.minimalj.model.annotation.NotEmpty;
import org.minimalj.model.properties.PropertyInterface;
import org.minimalj.util.EqualsHelper;
import org.minimalj.util.GenericUtils;
import org.minimalj.util.resources.Resources;

public abstract class MultiSelectionFormElement<T> extends AbstractFormElement<List<T>> implements Enable {
	private final SwitchComponent component;
	private List<T> object;
	private boolean enabled = true;

	public MultiSelectionFormElement(PropertyInterface property) {
		this(property, Form.EDITABLE);
	}

	public MultiSelectionFormElement(List<T> key) {
		this(Keys.getProperty(key), Form.EDITABLE);
	}

	public MultiSelectionFormElement(List<T> key, boolean editable) {
		this(Keys.getProperty(key), editable);
	}

	public MultiSelectionFormElement(PropertyInterface property, boolean editable) {
		super(property);
		component = Frontend.getInstance().createSwitchComponent();
		height(1, 3);
	}
	
	protected abstract List<T> getValues();
	
	protected List<T> getUnselectedValues() {
		List<T> values = getValues();
		if (values == null || object == null || object.isEmpty()) {
			return values;
		}
		values = new ArrayList<>(values);
		values.removeIf(v1 -> object.stream().anyMatch(v2 -> EqualsHelper.equals(v1, v2)));
		return values;
	}
	
	@Override
	public List<T> getValue() {
		return object;
	}

	@Override
	public void setValue(List<T> object) {
		this.object = object;
		handleChange();
	}

	@Override
	public IComponent getComponent() {
		return component;
	}

	protected void handleChange() {
		display();
		super.fireChange();
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		if (this.enabled != enabled) {
			this.enabled = enabled;
			if (!enabled && object != null) {
				object.clear();
				handleChange();
			} else {
				display();
			}
		}
	}

	private void display() {
		if (object != null && object.size() > 0) {
			IComponent[] components = new IComponent[object.size()];
			int index = 0;
			for (T item : object) {
				// editable
				ActionGroup actionGroup = new ActionGroup(null);
				for (Action a : getActions()) {
					actionGroup.add(a);
				}
				for (Action a : getActions(item)) {
					actionGroup.add(a);
				}
				Input<String> text = Frontend.getInstance().createReadOnlyTextField();
				text.setValue(render(item).toString());
				components[index++] = Frontend.getInstance().createLookup(text, actionGroup);
			}
			component.show(Frontend.getInstance().createVerticalGroup(components));
		} else {
			ActionGroup actionGroup = new ActionGroup(null);
			for (Action a : getActions()) {
				actionGroup.add(a);
			}
			Input<String> text = Frontend.getInstance().createReadOnlyTextField();
			text.setValue("");
			if (enabled) {
				component.show(Frontend.getInstance().createLookup(text, actionGroup));
			} else {
				component.show(text);
			}
		}
	}

	protected CharSequence render(T item) {
		return Rendering.render(item);
	}

	protected Action[] getActions() {
		if (!getUnselectedValues().isEmpty()) {
			return new Action[] { new AddListEntryEditor() };
		} else {
			return new Action[0];
		}
	}
	
	protected List<Action> getActions(T entry) {
		List<Action> list = new ArrayList<>();
		list.add(new RemoveEntryAction(entry));
		return list;
	}

	public class AddListEntryEditor extends NewObjectEditor<AddListEntryEditorModel<T>> {
		
		public AddListEntryEditor() {
		}

		public AddListEntryEditor(String name) {
			super(name);
		}
		
		@Override
		protected Object[] getNameArguments() {
			return new Object[] {
					Resources.getString(GenericUtils.getGenericClass(MultiSelectionFormElement.this.getClass()))
			};
		}
		
		@Override
		protected Form<AddListEntryEditorModel<T>> createForm() {
			Form<AddListEntryEditorModel<T>> form = new Form<>(Form.EDITABLE, 1, getColumnWidth());
			ComboBoxFormElement<T> comboBoxFormElement = new ComboBoxFormElement<>(Keys.getProperty(AddListEntryEditorModel.$.value), getUnselectedValues()) {
				public String getCaption() {
					return Resources.getString(GenericUtils.getGenericClass(MultiSelectionFormElement.this.getClass()));
				};
			};
			form.line(comboBoxFormElement);
			return form;
		}
		
		@Override
		public AddListEntryEditorModel<T> save(AddListEntryEditorModel<T> entry) {
			addEntry(entry.value);
			return entry;
		}

		@Override
		protected void finished(AddListEntryEditorModel<T> result) {
			handleChange();
		}
	}
	
	protected int getColumnWidth() {
		return Form.DEFAULT_COLUMN_WIDTH;
	}

	public static class AddListEntryEditorModel<U> {
		@SuppressWarnings("rawtypes")
		public static final AddListEntryEditorModel $ = Keys.of(AddListEntryEditorModel.class);
		
		@NotEmpty
		public U value;
	}

	
	protected void addEntry(T entry) {
		if (object == null) {
			object = new ArrayList<>();
		}
		object.add(entry);
	}
	
	protected class RemoveEntryAction extends Action {
		private final T entry;
		
		public RemoveEntryAction(T entry) {
			this.entry = entry;
		}
		
		@Override
		public void run() {
			removeEntry(entry);
			handleChange();
		}
    };

	protected void removeEntry(T entry) {
		getValue().remove(entry);
	}

}
