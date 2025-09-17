from pydantic import BaseModel

class MissionTemplate(BaseModel):
    template_id: int
    name: str
    render_str: str
