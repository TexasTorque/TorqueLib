#!/usr/bin/env python3
#
# MetaLogger
#
# (c) Justus Languell 2022

import os
import datetime
from random import seed

LOG_KEY = '@Log'

def capitalize_first(str):
    return str[0].upper() + str[1:]

def split_pascal_case(str):
    words = [[str[0]]]
 
    for c in str[1:]:
        if words[-1][-1].islower() and c.isupper():
            words.append(list(c))
        else:
            words[-1].append(c)
 
    # return [''.join(word) for word in words]
    return [''.join(word) for word in words]

def split_camel_case(str):
    return split_pascal_case(capitalize_first(str))

_types = {
    'double': 'Number',
    'int': 'Number',
    'float': 'Number',
    'long': 'Number',
    'string': 'String',
    'boolean': 'Boolean',
}

def error(msg):
    print(msg)
    exit(1)

def remove_block_comments(source):
    comment = False
    new = ""
    next = ""
    for i, c in enumerate(source):
        if i < len(source) - 1:
            next = source[i + 1]
        else:
            break
        if c == '/' and next == '*':
            comment = True
            
        if c == '*' and next == '/':                
            comment = False
            
        if not comment:
            new += c
    return new.replace('*/', '')

def remove_line_comments(source):
    new = ""
    lines = source.split('\n')
    for line in lines:
        parts = line.split('//')
        if len(parts) <= 1:
            new += line + '\n'
        else:
            new += parts[0] + '\n'
    return new
            
            
def logging_process(lines):
    logging_lines = []
    for i, line in enumerate(lines):
        print(line)
        if LOG_KEY in line:
            logging_lines.append(line.strip())

    commands = []
    for line in logging_lines:
        _type = ""
        expressions = []
        parts = line.split(' ')
        for i, part in enumerate(parts):
            if part == LOG_KEY:
                if not _type:
                    _type = parts[i + 1]
                    
        parts = line.split(',')
        for i, part in enumerate(parts):
            equals = part.split('=')
            ps = equals[0].strip().split(' ')
            expressions.append(ps[-1])
        for expression in expressions:
            title = ' '.join(split_camel_case(expression))
            commands.append(f'SmartDashboard.put{_types[_type]}("{title}", {expression});')
            print(commands[-1])
            
    return commands
    

def process(path, source):
    if path != "/Users/justuslanguell/TexasTorque/Robots/Clutch-2022/src/main/java/org/texastorque/subsystems/Turret.java": 
        return
    lines = remove_block_comments(remove_line_comments(source)).split('\n')
    commands = logging_process(lines)
    print(commands)

def validate(path):
    if 'package-info.java' in path:
        return
    source = open(path, 'r').read()
    source = process(path, source)
    # open(path, 'w').write(source)


if __name__ == '__main__':
    for root, subdirs, files in os.walk(os.path.realpath('./src/main/java/org/texastorque')):
        [validate(root + '/' + file) for file in files]
