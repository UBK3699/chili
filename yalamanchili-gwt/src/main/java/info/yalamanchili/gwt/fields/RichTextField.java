package info.yalamanchili.gwt.fields;

import info.yalamanchili.gwt.composite.BaseField;
import info.yalamanchili.gwt.widgets.RichTextToolBar;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.user.client.ui.RichTextArea;

public class RichTextField extends BaseField {
	RichTextArea area = new RichTextArea();
	RichTextToolBar bar = new RichTextToolBar(area);

	public RichTextField(String labelName, Boolean readOnly, Boolean required) {
		super(labelName, readOnly, required);
		configureAddMainWidget();
	}

	@Override
	protected void configureAddMainWidget() {
		area.addStyleName("y-gwt-RichTextEditor");
		bar.addStyleName("y-gwt-RichTexttoolBar");
		panel.insert(bar, 1);
		fieldPanel.insert(area, 0);
	}

	public String getValue() {
		return area.getText();
	}

	public void setValue(String value) {
		area.setText(value);
	}

	public void setHtml(String html) {
		area.setHTML(html);
	}

	public String getHtml() {
		return area.getHTML();
	}

	@Override
	public void onChange(ChangeEvent event) {
		// TODO Auto-generated method stub

	}
}
