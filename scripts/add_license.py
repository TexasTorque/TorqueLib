import os, datetime

license_email = 'jus@gtsbr.org'

def find_project(path):
    if 'torquelib' in path:
        return 'TorqueLib'
    parts = path.split('/')
    return parts[parts.index('src') - 1]

def get_project_year(project):
    if project == 'TorqueLib':
        return '2011-' + str(datetime.date.today().year)
    try:
        return str(int(project.split('-')[-1]))
    except:
        return str(datetime.date.today().year)

def make_jdoc(text):
    comment = '/**\n'
    for line in text.split('\n'):
        comment += ' * ' + line + '\n'
    return comment + ' */'

def get_license(path):
    project = find_project(path)
    disclaimer = 'not licensed for distribution'
    if project == 'TorqueLib':
        disclaimer = 'licensed under the MIT license'
        
    year = get_project_year(project)
    return f'''Copyright {year} Texas Torque.

This file is part of {project}, which is {disclaimer}.
For more details, see ./license.txt or write <{license_email}>.'''

def extension(path):
    return os.path.splitext(path)[-1]

put_license_before = 'package org.texastorque'

def with_license(path, source):
    license = get_license(path)
    if extension(path).endswith('java'):
        return make_jdoc(license) + '\n' + put_license_before + source.split(put_license_before)[-1]
    return source

def validate(path):
    if 'package-info.java' in path:
        return
    source = open(path, 'r').read()
    source = with_license(path, source)
    open(path, 'w').write(source)

if __name__ == '__main__':
    for root, subdirs, files in os.walk(os.path.realpath('./src/main/java/org/texastorque')):
        [validate(root + '/' + file) for file in files]

        