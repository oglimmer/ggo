# WebSocket Communication Documentation

## Overview
This document describes the WebSocket communication system and global data model for the GGO (Great Game Online) application.

## WebSocket Connection

### Connection Setup
- **Endpoint**: `/srvcom` 
- **Location**: `src/main/resources/static/js/app/Communication.js:17`
- **Protocol Detection**: Automatically detects `ws://` or `wss://` based on page protocol
- **URL Generation**: `protocol://host/srvcom`

### Connection Features
- **Reconnection**: Exponential backoff strategy (max 10 attempts, 2s-30s delay)
- **Connection State**: Tracked via `connectedToServer` boolean flag
- **Manual Disconnect**: Prevents reconnection on page unload
- **Error Handling**: Comprehensive error logging and state management

### Message Format
All WebSocket messages use JSON format:
```javascript
// Outgoing message structure
{
  pid: playerId,    // Player identifier
  cmd: "command",   // Command type
  param: value      // Command parameter (optional)
}

// Incoming messages are JSON objects merged into global model
```

## Global Data Model

### Structure
The global data model is defined in `src/main/resources/static/js/app/GlobalData.js:8-12`:

```javascript
var globalDataObject = {
  board: null,                    // Board instance for rendering
  playerId: window.ggoPlayerId,   // Current player ID
  model: {}                       // Game state data
};
```

### Model Properties
The `model` object contains the following key structures:

#### Board State (`model.boardState`)
- **`corToFields`**: Object mapping coordinate strings ("x:y") to Field objects
- **`idToUnits`**: Object mapping unit IDs to Unit objects  
- **`idToHanditems`**: Object mapping hand item IDs to HandItem objects
- **`idToButtons`**: Object mapping button IDs to Button objects
- **`showCoordinates`**: Boolean flag for coordinate display

#### Game State
- **`myColor`**: Current player's color/team
- **`modalDialogState`**: Current modal dialog state (if any)

## Data Merging System

### ObjectMerger (`src/main/resources/static/js/app/ObjectMerger.js`)
The ObjectMerger handles incoming server data and merges it into the global model:

#### Key Features
- **Deep Merging**: Recursively merges nested objects and arrays
- **Object Construction**: Creates new objects based on `jsClass` property
- **Removal Handling**: Supports `##REMOVED##` token for deletions
- **Type Safety**: Validates data types during merge operations

#### Merge Process
1. Server sends JSON data via WebSocket
2. `ResponseHandler.process()` calls `objectMerger.merge(jsonObj, globalData.model)`
3. Data is merged into existing global model structure
4. `globalData.board.draw()` is called to re-render the UI

## Game Entity Data Structures

### Fields
**Location**: `src/main/resources/static/js/app/Field.js`

Fields represent hexagonal game board positions:
```javascript
{
  id: "x:y",           // Coordinate identifier
  x: number,           // X coordinate
  y: number,           // Y coordinate
  selectable: boolean, // Can be clicked/selected
  highlight: boolean   // Visual highlight state
}
```

### Units
**Location**: `src/main/resources/static/js/app/Unit.js`

Units represent game pieces on the board:
```javascript
{
  id: string,              // Unique unit identifier
  color: string,           // Player color/team
  unitType: string,        // Unit type (infantry, tank, etc.)
  x: number,              // Current X position
  y: number,              // Current Y position
  selected: boolean,       // Currently selected state
  selectable: boolean,     // Can be selected by player
  command: {              // Pending command (if any)
    commandType: string,   // "M" (move), "B" (build), "S" (shoot), "F" (fortify)
    x: number,            // Target X coordinate
    y: number             // Target Y coordinate
  }
}
```

### Hand Items
**Location**: `src/main/resources/static/js/app/HandItem.js`

Hand items represent units available for deployment:
```javascript
{
  id: string,              // Unique item identifier
  unitType: string,        // Unit type to deploy
  selected: boolean,       // Currently selected
  selectable: boolean      // Can be selected
}
```

### Buttons
**Location**: `src/main/resources/static/js/app/Button.js`

Buttons provide game actions:
```javascript
{
  id: string,              // Unique button identifier
  text: string,            // Button display text
  graphic: string,         // Optional graphic identifier
  width: number,           // Button width
  height: number,          // Button height
  selectable: boolean,     // Can be clicked
  hidden: boolean          // Visibility state
}
```

## Rendering System

### Board Rendering (`src/main/resources/static/js/app/Board.js`)
The board renders in multiple layers:

1. **Fields** (`drawFields()`): Hexagonal grid background
2. **Units** (`drawUnits()`): Game pieces in 3 Z-levels (0, 1, 2)
3. **Hand** (`drawHand()`): Available units for deployment
4. **Buttons** (`drawButtons()`): Action buttons
5. **Modal Dialog** (`drawModalDialog()`): Popup dialogs

### Rendering Trigger
- **Automatic**: Called after every WebSocket message via `ResponseHandler.process()`
- **Manual**: Can be triggered via `globalData.board.draw()`

## WebSocket Message Types

### Outgoing Messages

#### Game Join
```javascript
{
  pid: globalData.playerId,
  cmd: "join"
}
```
**Trigger**: Connection established (`src/main/resources/static/js/board.js:22`)

#### Unit Selection
```javascript
{
  pid: globalData.playerId,
  cmd: "selectUnit",
  param: unitId
}
```
**Trigger**: Clicking selectable unit (`src/main/resources/static/js/app/Unit.js:142`)

#### Field Selection
```javascript
{
  pid: globalData.playerId,
  cmd: "selectTargetField",
  param: fieldId
}
```
**Trigger**: Clicking selectable field (`src/main/resources/static/js/app/Field.js:78`)

#### Hand Card Selection
```javascript
{
  pid: globalData.playerId,
  cmd: "selectHandCard",
  param: itemId
}
```
**Trigger**: Clicking selectable hand item (`src/main/resources/static/js/app/HandItem.js:79`)

#### Button Actions
```javascript
{
  pid: globalData.playerId,
  cmd: "button",
  param: buttonId
}
```
**Trigger**: Clicking selectable button (`src/main/resources/static/js/app/Button.js:58`)

#### Modal Dialog Selection
```javascript
{
  pid: globalData.playerId,
  cmd: "selectModalDialog",
  param: optionId
}
```
**Trigger**: Clicking modal dialog option (`src/main/resources/static/js/app/ModalDialog.js:81`)

### Incoming Messages

#### Server Response Format
Server responses are JSON objects that get merged into `globalData.model`. Common patterns:

```javascript
{
  boardState: {
    corToFields: {
      "0:0": { id: "0:0", selectable: true, highlight: false },
      "1:0": { id: "1:0", selectable: false, highlight: true }
    },
    idToUnits: {
      "unit1": { 
        id: "unit1", 
        x: 0, 
        y: 0, 
        selected: true, 
        selectable: false,
        command: { commandType: "M", x: 1, y: 0 }
      }
    },
    idToHanditems: {
      "hand1": { id: "hand1", unitType: "infantry", selectable: true }
    },
    idToButtons: {
      "btn1": { id: "btn1", text: "End Turn", selectable: true, hidden: false }
    }
  },
  myColor: "red",
  modalDialogState: {
    show: true,
    title: "Select Action",
    options: [
      { id: "move", description: "Move Unit" },
      { id: "attack", description: "Attack" }
    ]
  }
}
```

## Message Flow

1. **User Interaction**: Player clicks on game element
2. **Event Handler**: Element's `onSelect()` method is called
3. **WebSocket Send**: Message sent to server via `Communication.send()`
4. **Server Processing**: Server processes command and updates game state
5. **Server Response**: Server sends updated game state via WebSocket
6. **Data Merge**: `ObjectMerger.merge()` updates global model
7. **UI Update**: `globalData.board.draw()` re-renders the game board

## Selection States

### Field Selection
- **Selectable**: Field can be clicked (usually for unit movement targets)
- **Highlight**: Field is visually highlighted (often showing possible moves)

### Unit Selection
- **Selected**: Unit is currently selected (shows selection indicator)
- **Selectable**: Unit can be selected by the player
- **Command**: Unit has a pending command (shows command arrows/indicators)

### Interactive Elements
- **Hand Items**: Available units for deployment
- **Buttons**: Game actions (End Turn, Cancel, etc.)
- **Modal Dialogs**: Context-sensitive option menus

## Error Handling

### Connection Errors
- Automatic reconnection with exponential backoff
- Connection state tracking
- Error logging to console

### Data Validation
- Type checking during merge operations
- Assertion failures for invalid data
- Graceful handling of missing properties

## Performance Considerations

### Rendering Optimization
- Canvas-based rendering for smooth performance
- Layered rendering system for proper Z-ordering
- Efficient click detection using coordinate mapping

### Data Management
- Efficient object merging to minimize DOM updates
- Selective re-rendering only when data changes
- Memory management for game objects