import os

OUT = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))),
                   "src", "main", "java", "view", "CalendarPanel.form")

HEADER = '''<?xml version="1.0" encoding="UTF-8" ?>

<Form version="1.8" maxVersion="1.9" type="org.netbeans.modules.form.forminfo.JPanelFormInfo">
  <Properties>
    <Property name="background" type="java.awt.Color" editor="org.netbeans.beaninfo.editors.ColorEditor">
      <Color blue="ff" green="ff" red="ff" type="rgb"/>
    </Property>
    <Property name="preferredSize" type="java.awt.Dimension" editor="org.netbeans.beaninfo.editors.DimensionEditor">
      <Dimension value="[332, 348]"/>
    </Property>
  </Properties>
  <AuxValues>
    <AuxValue name="FormSettings_autoResourcing" type="java.lang.Integer" value="0"/>
    <AuxValue name="FormSettings_autoSetComponentName" type="java.lang.Boolean" value="false"/>
    <AuxValue name="FormSettings_generateFQN" type="java.lang.Boolean" value="true"/>
    <AuxValue name="FormSettings_generateMnemonicsCode" type="java.lang.Boolean" value="false"/>
    <AuxValue name="FormSettings_i18nAutoMode" type="java.lang.Boolean" value="false"/>
    <AuxValue name="FormSettings_layoutCodeTarget" type="java.lang.Integer" value="1"/>
    <AuxValue name="FormSettings_listenerGenerationStyle" type="java.lang.Integer" value="0"/>
    <AuxValue name="FormSettings_variablesLocal" type="java.lang.Boolean" value="false"/>
    <AuxValue name="FormSettings_variablesModifier" type="java.lang.Integer" value="2"/>
  </AuxValues>

'''


def component(name):
    return ('              <Component id="%s" min="-2" max="-2" attributes="0"/>\n'
            % name)


def wide(name):
    return '              <Component id="%s" max="32767" attributes="0"/>\n' % name


def empty():
    return '              <EmptySpace max="-2" attributes="0"/>\n'


def glue():
    return '              <EmptySpace max="32767" attributes="0"/>\n'


def seq(items):
    out = ['          <Group type="102" alignment="0" attributes="0">\n']
    out.extend(items)
    out.append('          </Group>\n')
    return "".join(out)


def baseline(names):
    out = ['                  <Group type="103" groupAlignment="3" attributes="0">\n']
    for name in names:
        out.append('                      <Component id="%s" alignment="3" min="-2" max="-2" attributes="0"/>\n' % name)
    out.append('                  </Group>\n')
    return "".join(out)


def container(name, horizontal, vertical, subcomponents, indent=4):
    pad = " " * indent
    text = []
    text.append('%s<Container class="javax.swing.JPanel" name="%s">\n' % (pad, name))
    text.append('%s  <Properties>\n' % pad)
    text.append('%s    <Property name="background" type="java.awt.Color" editor="org.netbeans.beaninfo.editors.ColorEditor">\n' % pad)
    text.append('%s      <Color blue="ff" green="ff" red="ff" type="rgb"/>\n' % pad)
    text.append('%s    </Property>\n' % pad)
    text.append('%s  </Properties>\n' % pad)
    text.append('%s  <Layout>\n' % pad)
    text.append('%s    <DimensionLayout dim="0">\n' % pad)
    text.append('%s      <Group type="103" groupAlignment="0" attributes="0">\n' % pad)
    text.append(horizontal)
    text.append('%s      </Group>\n' % pad)
    text.append('%s    </DimensionLayout>\n' % pad)
    text.append('%s    <DimensionLayout dim="1">\n' % pad)
    text.append('%s      <Group type="103" groupAlignment="0" attributes="0">\n' % pad)
    text.append(vertical)
    text.append('%s      </Group>\n' % pad)
    text.append('%s    </DimensionLayout>\n' % pad)
    text.append('%s  </Layout>\n' % pad)
    if subcomponents:
        text.append('%s  <SubComponents>\n' % pad)
        text.extend(subcomponents)
        text.append('%s  </SubComponents>\n' % pad)
    text.append('%s</Container>\n' % pad)
    return "".join(text)


def label(name, text, extra=""):
    return ('    <Component class="javax.swing.JLabel" name="%s">\n'
            '      <Properties><Property name="text" type="java.lang.String" value="%s"/></Properties>\n'
            '    </Component>\n' % (name, text))


def button(name, text):
    return ('    <Component class="javax.swing.JButton" name="%s">\n'
            '      <Properties>\n'
            '        <Property name="text" type="java.lang.String" value="%s"/>\n'
            '        <Property name="focusable" type="boolean" value="false"/>\n'
            '      </Properties>\n'
            '    </Component>\n' % (name, text))


def build():
    root_h = []
    for name in ("lblTitle", "pnlNavigation", "pnlWeekdays", "pnlDays", "pnlFooter"):
        root_h.append(seq([empty(), wide(name) if name != "lblTitle" else component(name),
                           glue() if name == "lblTitle" else empty()]))

    root_v = [seq([empty(),
                   component("lblTitle"), empty(),
                   component("pnlNavigation"), empty(),
                   component("pnlWeekdays"), empty(),
                   '              <Component id="pnlDays" min="-2" pref="210" max="32767" attributes="0"/>\n', empty(),
                   component("pnlFooter"), empty()])]

    nav_h = [seq([component("btnPrev"), empty(), component("cmbMonth"), empty(),
                  component("cmbYear"), empty(), component("btnNext"), glue()])]
    nav_v = [seq([baseline(["btnPrev", "cmbMonth", "cmbYear", "btnNext"]), glue()])]

    days = ["Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min"]
    week_h = [seq([item for name in days for item in (component("lbl" + name), empty())][:-1] + [glue()])]
    week_v = [seq([baseline(["lbl" + name for name in days]), glue()])]

    foot_h = [seq([glue(), component("btnToday"), empty(), component("btnCancel")])]
    foot_v = [seq([baseline(["btnToday", "btnCancel"]), glue()])]

    parts = [HEADER]
    parts.append("  <Layout>\n")
    parts.append('    <DimensionLayout dim="0">\n')
    parts.append('      <Group type="103" groupAlignment="0" attributes="0">\n')
    parts.extend(root_h)
    parts.append('      </Group>\n')
    parts.append('    </DimensionLayout>\n')
    parts.append('    <DimensionLayout dim="1">\n')
    parts.append('      <Group type="103" groupAlignment="0" attributes="0">\n')
    parts.extend(root_v)
    parts.append('      </Group>\n')
    parts.append('    </DimensionLayout>\n')
    parts.append("  </Layout>\n")
    parts.append("  <SubComponents>\n")
    parts.append('    <Component class="javax.swing.JLabel" name="lblTitle">\n'
                 '      <Properties>\n'
                 '        <Property name="font" type="java.awt.Font" editor="org.netbeans.beaninfo.editors.FontEditor">\n'
                 '          <Font name="Segoe UI" size="14" style="1"/>\n'
                 '        </Property>\n'
                 '        <Property name="text" type="java.lang.String" value="Pilih Tanggal"/>\n'
                 '      </Properties>\n'
                 '    </Component>\n')
    parts.append(container("pnlNavigation", "".join(nav_h), "".join(nav_v), [
        button("btnPrev", "&lt;"),
        '    <Component class="javax.swing.JComboBox" name="cmbMonth"/>\n',
        '    <Component class="javax.swing.JComboBox" name="cmbYear"/>\n',
        button("btnNext", "&gt;"),
    ]))
    parts.append(container("pnlWeekdays", "".join(week_h), "".join(week_v),
                           [label("lbl" + name, name) for name in days]))
    parts.append(container("pnlDays", "", "", []))
    parts.append(container("pnlFooter", "".join(foot_h), "".join(foot_v), [
        button("btnToday", "Hari Ini"),
        button("btnCancel", "Batal"),
    ]))
    parts.append("  </SubComponents>\n</Form>\n")
    return "".join(parts)


with open(OUT, "w", encoding="utf-8", newline="\n") as handle:
    handle.write(build())
print("wrote", OUT)
