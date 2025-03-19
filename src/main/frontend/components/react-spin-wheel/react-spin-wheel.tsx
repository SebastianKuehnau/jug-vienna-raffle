import { ReactAdapterElement, RenderHooks } from 'Frontend/generated/flow/ReactAdapter';
import { ReactElement } from 'react';
import { SpinWheel } from 'react-spin-wheel';
import "react-spin-wheel/dist/index.css";

class ReactSpinWheelElement extends ReactAdapterElement {
  protected override render(hooks: RenderHooks): ReactElement | null {

    const [items, setItems] = hooks.useState<string[]>('items');
    const onFinishSpinEvent = hooks.useCustomEvent<string>("onFinishSpin");

    return <SpinWheel items={items}
                      onFinishSpin={result => onFinishSpinEvent(result as string)}
                      spinContainerStyle={{width: "1000px", height: "1000px"}}/>; // (3)
  }
}

customElements.define('react-spin-wheel', ReactSpinWheelElement); // (4)