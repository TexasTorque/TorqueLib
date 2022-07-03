#!/usr/bin/env python3

# Script to help me generate methods for TorqueController.java

map_ = [1, 0, 5, 4, 6, 6, 9, 10, 5, 6, 2, 3, 7, 8, 3, 4, 2, 1];

buttons = {
    'left stick click': 6,
    'right stick click': 7,
    'left bumper': 8,
    'right bumper': 9,
    'left center button': 12,
    'right center button': 13,
    'X button': 14,
    'Y button': 15,
    'B button': 16,
    'A button': 17
}

def gen_buttons(): 
    print('// * DEPRECATED Button interface functions')
    for button, index in buttons.items(): 
        print(f'''/**
 * Check if the {button.replace(' click', '')} is currently down.
 * 
 * @return Is the {button.replace(' click', '')} currently down?
 * 
 * @deprecated Here for old API compatibility. 
 *             Use the "is{button.title().replace(' ', '')}Down" method instead
 */
@Deprecated
public final boolean get{button.title().replace(' ', '')}() {{
    return down({map_[index]});
}}
''')
    print('// * Button down interface functions') 
    for button, index in buttons.items(): 
        print(f'''/**
 * Check if the {button.replace('click', '')} is currently down.
 * 
 * @return Is the {button.replace('click', '')} currently down?
 */
public final boolean is{button.title().replace(' ', '')}Down() {{
    return down({map_[index]});
}}
''')
    print('// * Button pressed interface functions') 
    for button, index in buttons.items(): 
        print(f'''/**
 * Check if the {button.replace('click', '')} is being pressed.
 * 
 * @return Is the {button.replace('click', '')} being pressed?
 */
public final boolean is{button.title().replace(' ', '')}Pressed() {{
    return pressed({map_[index]});
}}
''')
    print('// * Button released interface functions')
    for button, index in buttons.items(): 
        print(f'''/**
 * Check if the {button.replace('click', '')} is being released.
 * 
 * @return Is the {button.replace('click', '')} being released?
 */
public final boolean is{button.title().replace(' ', '')}Released() {{
    return released({map_[index]});
}}
''')
    
if __name__ == '__main__':
    gen_buttons()