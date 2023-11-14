import coronaLogo from "/corona.svg";

export default function Logo() {
    return (
        <h1
            style={{
                display: "flex",
                gap: "20px",
                alignItems: "center",
                minWidth: "300px",
            }}
        >
            <span>Covid visualizer</span>
            <img width="50px" src={coronaLogo} alt="Vite" />
        </h1>
    );
}
