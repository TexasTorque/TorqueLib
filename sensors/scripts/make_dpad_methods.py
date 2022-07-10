#!/usr/bin/env python3

# Script to help me generate methods for TorqueController.java

dpad = ['UP', 'UP_RIGHT', 'RIGHT', 'DOWN_RIGHT', 'DOWN', 'DOWN_LEFT', 'LEFT', 'UP_LEFT']

def generate_dpad():
    for i, key in enumerate(dpad):
        print(f'''/**
 * Check if the DPAD {key.lower().replace('_', ' ')} button is being held down.
 * 
 * @return Is the DPAD {key.lower().replace('_', ' ')} button being held down?
 *
 * @deprecated Here for old API compatibility. 
 *             Use the "is{key.title().replace('_', '')}Down()" method instead
 */
@Deprecated
public final boolean getDPAD{key.title().replace('_', '')}() {{
    return isDPAD{key.title().replace('_', '')}Down();
}}
''')
        
    for i, key in enumerate(dpad):
        print(f'''/**
 * Check if the DPAD {key.lower().replace('_', ' ')} button is being held down.
 * 
 * @return Is the DPAD {key.lower().replace('_', ' ')} button being held down?
 */
public final boolean isDPAD{key.title().replace('_', '')}Down() {{
    return stick.getPOV() == {i * 45};
}}
''')
    
if __name__ == '__main__':
    generate_dpad()