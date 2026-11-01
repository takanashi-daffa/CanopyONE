import os
import re

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SRC = os.path.join(ROOT, "src", "main", "java", "view", "CalendarPanel.form")
OUT = os.path.join(ROOT, "src", "main", "java", "view", "CalendarDialog.form")

with open(SRC, encoding="utf-8") as handle:
    text = handle.read()

text = text.replace(
    'type="org.netbeans.modules.form.forminfo.JPanelFormInfo"',
    'type="org.netbeans.modules.form.forminfo.JDialogFormInfo"')

properties = '''  <Properties>
    <Property name="defaultCloseOperation" type="int" value="2"/>
    <Property name="title" type="java.lang.String" value="Pilih Tanggal"/>
    <Property name="resizable" type="boolean" value="false"/>
  </Properties>
'''

text = re.sub(r"  <Properties>.*?  </Properties>\n", properties, text, count=1, flags=re.S)

with open(OUT, "w", encoding="utf-8", newline="\n") as handle:
    handle.write(text)

print("wrote", OUT)
