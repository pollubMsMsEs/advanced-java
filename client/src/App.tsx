import { useState, useEffect, ChangeEvent } from "react";
import coronaLogo from "/corona.svg";
import axios from "axios";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import { Line } from "react-chartjs-2";
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
} from "chart.js";

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend
);

async function sendImportRequest(url: string) {
  const toastId = toast.loading("Importing...");

  try {
    const result = await axios.put(url);
    console.log(result);

    if (result.data.error) {
      throw new Error(result.data.msg);
    }

    toast.update(toastId, {
      render: "Import succedded!",
      type: "success",
      isLoading: false,
      autoClose: 4000,
    });
  } catch (error: any) {
    toast.update(toastId, {
      render: `Import failed: ${error.message ?? ""}`,
      type: "error",
      isLoading: false,
      autoClose: 4000,
    });
  }
}

function App() {
  const [isImportingVaccinations, setIsImportingVaccinations] = useState(false);
  const [isImportingCases, setIsImportingCases] = useState(false);

  const [countryList, setCountryList] = useState<string[] | null>(null);

  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [chartData, setChartData] = useState<any | null>(null);
  const [selectedCountries, setSelectedCountries] = useState<string[]>([]);

  const handleStartDateChange = (event: ChangeEvent<HTMLInputElement>) => {
    setStartDate(event.target.value);
  };

  const handleEndDateChange = (event: ChangeEvent<HTMLInputElement>) => {
    setEndDate(event.target.value);
  };

  const generateDateRange = (startDate: string, endDate: string) => {
    const labels = [];
    const date1 = new Date(startDate);
    const date2 = new Date(endDate);

    while (date1 <= date2) {
      labels.push(date1.toISOString().slice(0, 10));
      date1.setDate(date1.getDate() + 1);
    }
    return labels;
  };

  const handleGenerateData = async () => {
    try {
        const countries = await axios.get("http://localhost:8000/api/countries");
        const countryIds = selectedCountries.map((countryName) => {
          const countryId = countries.data.data[countryName];
          return countryId;
        });

      const labels = generateDateRange(startDate, endDate);
      console.log(countryIds);
      const response = await axios.get("http://localhost:8000/api/cases", {
        params: {
          begin_date: startDate,
          end_date: endDate,
          countries: countryIds,
        },
      });
      const casesData = response.data;
      const cases = Object.values(casesData);
      const data = {
        labels: labels,
        datasets: [
          {
            label: 'New cases',
            data: cases,
            borderColor: '#f0f257',
            backgroundColor: '#f0f257',
          },
        ],
      };
      setChartData(data);
    } catch (error) {
      console.error(error);
      setChartData(null);
    }
  };

  const handleCountryCheckboxChange = (event: ChangeEvent<HTMLInputElement>) => {
    const { name, checked } = event.target;
    if (checked) {
      setSelectedCountries((prevSelectedCountries) => [
        ...prevSelectedCountries,
        name,
      ]);
    } else {
      setSelectedCountries((prevSelectedCountries) =>
        prevSelectedCountries.filter((country) => country !== name)
      );
    }
  };

  async function importCases() {
    setIsImportingCases(true);

    await sendImportRequest("http://localhost:8000/api/import/cases");

    setIsImportingCases(false);
  }

  async function importVaccinations() {
    setIsImportingVaccinations(true);

    await sendImportRequest("http://localhost:8000/api/import/vaccinations");

    setIsImportingVaccinations(false);
  }

  async function getCountriesList() {
    try {
      const countriesObj = await axios.get(
        "http://localhost:8000/api/countries"
      );
      const countryNames = Object.keys(countriesObj.data.data);
      setCountryList(countryNames);
    } catch (error) {
      console.error(error);
      setCountryList(null);
    }
  }
  useEffect(() => {
    getCountriesList();
  }, []);

  return (
    <div
      style={{
        display: "grid",
        height: "100vh",
        gridTemplateColumns: "250px 1fr",
      }}
    >
      <aside
        className="aside"
        style={{
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          gap: "10px",
        }}
      >
        <header style={{ display: "flex", gap: "20px" }}>
          <h1>Covid visualizer</h1>
          <img width="50px" src={coronaLogo} alt="Vite" />
        </header>
        <div
          style={{
            alignSelf: "stretch",
            marginTop: "-20px",
          }}
        >
          <hr />
        </div>
        <button
          onClick={importCases}
          disabled={isImportingCases || isImportingVaccinations}
          className="button"
          style={{
            color: "aliceblue",
            backgroundColor: isImportingCases ? "#a1a1aa" : "#0284c7",
            border: "none",
            padding: "10px",
            borderRadius: "5px",
          }}
        >
          Import Cases
        </button>

        <button
          onClick={importVaccinations}
          disabled={isImportingCases || isImportingVaccinations}
          className="button"
          style={{
            color: "aliceblue",
            backgroundColor: isImportingVaccinations ? "#a1a1aa" : "#0284c7",
            border: "none",
            padding: "10px",
            borderRadius: "5px",
          }}
        >
          Import Vaccinations
        </button>
        <ToastContainer position={toast.POSITION.BOTTOM_CENTER} />
      </aside>
      <main
        style={{
          padding: "10px",
          display: "grid",
          gridTemplateColumns: "150px 1fr",
          gridTemplateRows: "1fr 200px",
        }}
      >
        <div>
          {countryList?.map((country) => (
            <div key={country}>
              <span>{country}</span>
              <input 
              type="checkbox" 
              name={country} 
              id={country}          
              onChange={handleCountryCheckboxChange}
              />
            </div>
          )) ?? "Couldn't load countries"}

          <h3>Daty</h3>

          <label>Od:</label>
          <input
            type="date"
            id="date1"
            value={startDate}
            onChange={handleStartDateChange}
          />
         <br />      <br />
          <label>Do:</label>
          <input
            type="date"
            id="date2"
            value={endDate}
            onChange={handleEndDateChange}
          />
          <br /><br />
          <button onClick={handleGenerateData}>Pokaż wykres</button>
        </div>
        <div
          className="chart-container"
          style={{ position: "relative", width: "95%" }} //@Skic Required for charts to scale properly
        >
          {chartData ? (
            <Line data={chartData} />
          ) : (
            <p>Podaj dane, aby wyświetlić wykres</p>
          )}
        </div>
      </main>
    </div>
  );
}

export default App;
