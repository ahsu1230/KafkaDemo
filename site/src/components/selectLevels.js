import React from "react";

function SelectLevels(props) {
    const options = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10].map((level, index) => (
        <option key={index} value={level}>
            {"Level " + level}
        </option>
    ));
    return (
        <select onChange={props.onSelect} value={props.value}>
            {options}
        </select>
    );
}

export default SelectLevels;
