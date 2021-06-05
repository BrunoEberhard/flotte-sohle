package ch.openech.flottesohle.frontend;

import java.util.List;

import org.minimalj.backend.Backend;
import org.minimalj.frontend.form.Form;
import org.minimalj.frontend.form.element.PasswordFormElement;
import org.minimalj.frontend.page.SimpleTableEditorPage;
import org.minimalj.repository.query.By;

import ch.openech.flottesohle.model.FlotteSohleUser;

public class UserTablePage extends SimpleTableEditorPage<FlotteSohleUser> {

	@Override
	protected Object[] getColumns() {
		return new Object[] { FlotteSohleUser.$.email, FlotteSohleUser.$.name, FlotteSohleUser.$.vorname };
	}
	
	@Override
	protected List<FlotteSohleUser> load() {
		return Backend.find(FlotteSohleUser.class, By.all());
	}

	@Override
	public void action(FlotteSohleUser user) {
		// don't show detail on double click just open editor
		openEditor(user);
	}
	
	@Override
	protected Form<FlotteSohleUser> createForm(boolean editable, boolean newObject) {
		Form<FlotteSohleUser> form = new Form<>(editable, 2);
		form.line(FlotteSohleUser.$.email);
		form.line(FlotteSohleUser.$.vorname, FlotteSohleUser.$.name);
		if (editable && newObject) {
			form.line(new PasswordFormElement(FlotteSohleUser.$.password));
		}
		form.line(FlotteSohleUser.$.multiLocation);
//		form.line("Rollen");
//		for (FlotteSohleRoles r : FlotteSohleRoles.values()) {
//			form.line(new CheckBoxFormElement(new EnumSetFormElementProperty(r), EnumUtils.getText(r), true, false));
//		}
		return form;
	}
//	
//	private class EnumSetFormElementProperty extends CheckBoxProperty {
//		private final FlotteSohleRoles r;
//
//		public EnumSetFormElementProperty(FlotteSohleRoles r) {
//			this.r = Objects.requireNonNull(r);
//		}
//
//		@Override
//		public Class<?> getDeclaringClass() {
//			return User.class;
//		}
//
//		@Override
//		public String getName() {
//			return r.name();
//		}
//
//		@Override
//		public String getPath() {
//			return r.name();
//		}
//
//		@Override
//		public Class<?> getClazz() {
//			return User.class;
//		}
//
//		@Override
//		public Boolean getValue(Object object) {
//			User user = (User) object;
//			return user.roles.stream().anyMatch(userRole -> r.name().equals(userRole.name));
//		}
//
//		@Override
//		public void setValue(Object object, Object newValue) {
//			User user = (User) object;
//			if (Boolean.TRUE.equals(newValue)) {
//				if (!getValue(object)) {
//					user.roles.add(new UserRole(r.name()));
//				}
//			} else {
//				user.roles.removeIf(userRole -> r.name().equals(userRole.name));
//			}
//		}
//	}
	
}
