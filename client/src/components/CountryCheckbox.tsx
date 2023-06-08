export default function CountryCheckbox({
    country,
    checked,
    updateSelected,
    style,
}: {
    country: { id: number; name: string };
    checked: boolean;
    updateSelected: (updateData: { id: number; checked: boolean }) => void;
    style?: React.CSSProperties;
}) {
    return (
        <div style={style}>
            <input
                type="checkbox"
                name={country.name}
                checked={checked}
                onChange={(e) => {
                    const { checked } = e.target;
                    updateSelected({ id: country.id, checked });
                }}
            />
            <span>{country.name}</span>
        </div>
    );
}
