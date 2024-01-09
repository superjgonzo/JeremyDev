import Alert from "./components/Alert";
import Button from "./components/Button";
import ListGroup from "./components/ListGroup";
import { useState } from "react";

function App() {
  let items = ["New York", "Chicago", "San Francisco", "Tokyo", "Sweden"];

  const handleSelectItem = (item: String) => {
    console.log(item);
  };

  const [alertVisible, setAlertVisibility] = useState(false);

  return (
    <div>
      {alertVisible && (
        <Alert onCloseClick={() => setAlertVisibility(false)}>
          Hello <span>World</span>
        </Alert>
      )}
      <Button color="info" onClick={() => setAlertVisibility(true)}>
        Dynamic <span>Button Text</span>
      </Button>
      <ListGroup
        items={items}
        heading="Cities"
        onSelectItem={handleSelectItem}
      />
    </div>
  );
}

export default App;
