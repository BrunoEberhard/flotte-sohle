package ch.openech.dancer.frontend;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.minimalj.backend.Backend;
import org.minimalj.frontend.action.Action;
import org.minimalj.frontend.editor.Editor.NewObjectEditor;
import org.minimalj.frontend.form.Form;
import org.minimalj.frontend.page.TablePage;
import org.minimalj.repository.query.By;
import org.minimalj.repository.query.FieldOperator;
import org.minimalj.transaction.Role;

import ch.openech.dancer.model.AdminLog;
import ch.openech.dancer.model.AdminLog.AdminLogType;

@Role("admin")
public class AdminLogPage extends TablePage<AdminLog> {

	@Override
	protected Object[] getColumns() {
		return new Object[] { AdminLog.$.dateTime, AdminLog.$.logType, AdminLog.$.msg };
	}

	@Override
	protected List<AdminLog> load() {
		return Backend.find(AdminLog.class, //
				By.field(AdminLog.$.dateTime, FieldOperator.greater, LocalDateTime.now().minusDays(50)) //
						.order(AdminLog.$.dateTime, false));
	}

	@Override
	public List<Action> getTableActions() {
		return Collections.singletonList(new NewAdminLogAction());
	}

	private class NewAdminLogAction extends NewObjectEditor<AdminLog> {

		@Override
		protected Form<AdminLog> createForm() {
			Form<AdminLog> form = new Form<>();
			form.line(Form.readonly(AdminLog.$.dateTime));
			form.line(AdminLog.$.msg);
			return form;
		}

		@Override
		protected AdminLog createObject() {
			return new AdminLog(AdminLogType.MESSAGE, "");
		}

		@Override
		protected AdminLog save(AdminLog object) {
			return Backend.save(object);
		}

		@Override
		protected void finished(AdminLog result) {
			AdminLogPage.this.refresh();
		}
	}
}
